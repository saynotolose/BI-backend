package com.yupi.springbootinit.manager;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import io.github.briqt.spark4j.constant.SparkApiVersion;


/**
 * 讯飞星火大模型调用，预设等
 */
@Component
@Slf4j
public class XunFeiAIManager {

    @Resource
    SparkClient  sparkClient;

    //AI的预设条件
    public static final String PRECONDITION = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
            "分析需求：\n" +
            "{数据分析的需求或者目标}\n" +
            "原始数据：\n" +
            "{csv格式的原始数据，用,作为分隔符}\n" +
            "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
            "￥￥￥￥￥\n" +
            "{前端 Echarts库V5版本的 option 配置对象json代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释。只要图表内容的代码，代码前面不要加 option = 这种变量声明格式}\n" +
            "￥￥￥￥￥\n" +
            "{明确的数据分析结论，越详细越好，不要生成多余的注释\n}" +
            "最终格式是：￥￥￥￥￥前端代码￥￥￥￥￥分析结论" +
            "强调一下，最终生成的前端代码前面不要有变量的定义";

    /**
     * 构造请求，并发送给ai
     * @param content
     * @return
     */
    public String sendMesToAI(final String content) {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        //系统预设
        messages.add(SparkMessage.systemContent(PRECONDITION));
        //用户请求文本内容
        messages.add(SparkMessage.userContent(content));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度，非必传，默认为2048
                .maxTokens(2048)
                // 结果随机性，取值越高随机性越强，即相同的问题得到的不同答案的可能性越高，非必传，取值为[0,1]，默认为0.5
                .temperature(0.2)
                // 指定请求版本
                .apiVersion(SparkApiVersion.V3_5)
                .build();

        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        String responseContent = chatResponse.getContent();
        log.info("星火AI返回的结果{}", responseContent);
        return responseContent;
    }



}
