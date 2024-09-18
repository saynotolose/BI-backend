package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import static com.yupi.springbootinit.bizmq.BiMqConstant.*;

/**
 * 创建用到的交换机和队列
 */
public class BiInitMain {
    public static void main(String[] args) {
        try {
            //创建连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            //创建连接和通道
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();


            //声明交换机的名称和类型
            channel.exchangeDeclare(BI_EXCHANGE_NAME, "direct");

            //创建队列, 设置队列持久化，非独占，非自动删除，并且传入的额外参数为 null
            channel.queueDeclare(BI_QUEUE_NAME, true, false, false, null);

            //将队列绑定到交换机，指定路由键 "myRoutingKey"
            channel.queueBind(BI_QUEUE_NAME, BI_EXCHANGE_NAME, BI_ROUTING_KEY);


        } catch (Exception e) {
            //异常处理
        }
    }
}
