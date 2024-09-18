package com.yupi.springbootinit.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yupi.springbootinit.bizmq.BiMessageConsumer;
import com.yupi.springbootinit.bizmq.BiMessageProducer;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.ChartStatus;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.RedisLimiterManager;
import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.BiResponse;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

import static com.yupi.springbootinit.constant.FileConstant.ONE_MB;
import static com.yupi.springbootinit.constant.FileConstant.VALID_FILE_SUFFIX_LIST;

/**
* @author oykk
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-06-27 22:06:26
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements ChartService {

    @Resource
    BiMessageProducer biMessageProducer;    //mq生产者
    @Resource
    UserServiceImpl userService;
    @Resource
    RedisLimiterManager redisLimiterManager;


    /**
     * RabbitMQ 异步生成图表
     * @param multipartFile         用户上传的数据表
     * @param genChartByAiRequest   用户需求
     * @param request
     * @return
     */
    @Override
    public BiResponse geChartByAiAsyncMQ(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        //插件生成get代码
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        //参数校验
        //分析目标不应为空
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        //名称不为空且长度大于100，抛异常
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        //文件校验，大小和后缀
        //校验大小
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小超过1MB");
        //校验文件后缀
        String fileName_Original = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(fileName_Original);
        ThrowUtils.throwIf(!VALID_FILE_SUFFIX_LIST.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");

        /**
         * 限流
         */
        User currentUser = userService.getLoginUser(request);
        //针对每个用户的某个具体方法进行限流
        redisLimiterManager.doRateLimit("genChartByAi_" + currentUser.getId());

        //拿到csv格式数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);


        //先把图表保存到数据库，genChart和genResult先不设置
        Chart chart = new Chart();
        //插件生成全部set属性
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        //设置任务状态为等待中
        chart.setStatus(ChartStatus.WAIT);
        chart.setUserId(userService.getLoginUser(request).getId());
        boolean saveRes = save(chart);
        ThrowUtils.throwIf(!saveRes, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        long newChartId = chart.getId();

        //向消息队列里发送消息
        biMessageProducer.sendMessage(String.valueOf(newChartId));

        //返回结果
        BiResponse biResponse = new BiResponse();
        //biResponse.setGenChart(genChart);
        //biResponse.setGenResult(genResult);
        //chart表的id插入后雪花算法自动生成
        biResponse.setChartId(newChartId);
        return biResponse;
    }

    /**
     * 图表状态更新失败的处理
     * @param chartId
     * @param execMessage
     */
    public void handleChartUpdateError(long chartId, String execMessage){
        Chart updateResChart = new Chart();
        updateResChart.setId(chartId);
        updateResChart.setStatus(ChartStatus.FAILED);
        updateResChart.setExecMessage(execMessage);
        if (!updateById(updateResChart)){
            log.error("更新图表状态为失败，操作失败" + chartId + "," + execMessage);
        }
    }
}




