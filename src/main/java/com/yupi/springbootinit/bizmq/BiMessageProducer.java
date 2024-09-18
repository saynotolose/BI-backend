package com.yupi.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yupi.springbootinit.bizmq.BiMqConstant.BI_EXCHANGE_NAME;
import static com.yupi.springbootinit.bizmq.BiMqConstant.BI_ROUTING_KEY;

@Component
public class BiMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发消息的方法
     * @param message       消息内容，要发生的具体消息
     */
    public void sendMessage( String message) {
        //发送到指定交换机
        rabbitTemplate.convertAndSend(BI_EXCHANGE_NAME, BI_ROUTING_KEY, message);
    }
}
