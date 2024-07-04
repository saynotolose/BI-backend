package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class XunFeiAIManagerTest {

    @Resource
    XunFeiAIManager xunFeiAIManager;
    public final String userContext =
            "分析需求：\n" +
                    "分析杭州天气温度变化情况\n" +
                    "请使用：折线图\n" +
                    "原始数据：\n" +
                    "日期，温度\n" +
                    "1号,25摄氏度\n" +
                    "2号,32摄氏度\n" +
                    "3号,35摄氏度";


    @Test
    void sendMesToAI() {
        //不用打印结果了，这个方法会在日志中输出结果
        xunFeiAIManager.sendMesToAI(userContext);


    }
}