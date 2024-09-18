package com.yupi.springbootinit.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static com.yupi.springbootinit.bizmq.BiMqConstant.BI_EXCHANGE_NAME;
import static com.yupi.springbootinit.bizmq.BiMqConstant.BI_ROUTING_KEY;

@SpringBootTest
class MyMessageProducerTest {

    @Resource
    private BiMessageProducer biMessageProducer;
    @Test
    void sendMessage() {
        //biMessageProducer.sendMessage(BI_EXCHANGE_NAME, BI_ROUTING_KEY, "你好你好");
    }
}