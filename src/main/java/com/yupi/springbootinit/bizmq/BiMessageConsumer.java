package com.yupi.springbootinit.bizmq;


import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.ChartStatus;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.XunFeiAIManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.service.impl.ChartServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yupi.springbootinit.bizmq.BiMqConstant.BI_QUEUE_NAME;

/**
 * MQ消费者
 */
@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartServiceImpl chartService;
    @Resource
    private XunFeiAIManager xunFeiAIManager;


    /**
     * 接收消息的方法
     * @param message       接收的消息内容，字符串类型
     * @param channel       消息所在通道，可借助该通道与RabbitMQ交互，如手动确认消息，拒绝消息等
     * @param deliveryTag   消息投递标签，唯一标识一条消息
     */
    @SneakyThrows                                                   //该注解简化异常处理
    @RabbitListener(queues = {BI_QUEUE_NAME}, ackMode = "MANUAL")    //指定消费者方法，指定确认机制为手动
    public void receiveMessage(String message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){

        log.info("receiveMessage message = {}", message);
        //投递标签是一种数字标识，消费者接收到消息后用于向RabbitMQ确认消息的处理状态
        //通过将投递标签传递给basicAck方法，可以告知RabbitMQ该消息已经成功处理，可以进行确认和从队列中删除

        if (StringUtils.isBlank(message)){
            //如果更新失败，拒绝当前消息，让消息重新进入队列？？
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null){
            //如果图表为空，拒绝消息并抛出异常
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }
        //先修改图表状态为执行中
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(ChartStatus.RUNNING);
        //更新失败情况（数据库可能出问题了）
        if (!chartService.updateById(updateChart)){
            chartService.handleChartUpdateError(chart.getId(), "更新图表状态为执行中，操作失败");
            return;
        }
        //调用ai处理,返回结果
        String aiRes = xunFeiAIManager.sendMesToAI(buildUserInput(chart));
        String[] splitsRes = aiRes.split("￥￥￥￥￥");
        ThrowUtils.throwIf(splitsRes.length < 3, ErrorCode.SYSTEM_ERROR, "AI生成错误");
        String genChart = splitsRes[1].trim();
        String genResult = splitsRes[2].trim();
        //ai生成成功后，更新图表内容，更新任务状态为成功
        Chart updateSuccessChart = new Chart();
        updateSuccessChart.setId(chart.getId());
        updateSuccessChart.setGenChart(genChart);
        updateSuccessChart.setGenResult(genResult);
        updateSuccessChart.setStatus(ChartStatus.SUCCEED);
        if ( !chartService.updateById(updateSuccessChart)) {
            //如果图表更新失败，拒绝消息并抛出异常
            channel.basicNack(deliveryTag, false, false);
            chartService.handleChartUpdateError(chart.getId(),"更新图表状态为生成成功，操作失败");
            return;
        }


        //手动确认消息的接收，向RabbitMQ发送确认消息
        channel.basicAck(deliveryTag, false);
    }

    /**
     * 构建用户输入
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();

        //构造用户输入 userInput
        //头部
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求").append("\n");
        //拼接分析目标
        String origin_goal = goal;
        if (StringUtils.isNotBlank(chartType)){
            goal = goal + "，请使用" + chartType;
        }
        userInput.append(goal).append("\n");
        //拼接压缩后数据
        userInput.append(csvData).append("\n");
        return userInput.toString();

    }




}
