package com.yupi.springbootinit.config;


import io.github.briqt.spark4j.SparkClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 讯飞星火大模型配置类
 */
@Configuration
@ConfigurationProperties(prefix = "xunfei.client")
@Data
public class XunFeiAIConfig {

    private String appid;
    private String apiSecret;
    private String apiKey;

    @Bean
    //@Bean 注解用于方法上，该方法返回一个容器管理的 bean 对象
    public SparkClient sparkClient() {
        SparkClient sparkClient = new SparkClient();
        sparkClient.apiKey = apiKey;
        sparkClient.apiSecret = apiSecret;
        sparkClient.appid = appid;
        return sparkClient;
    }

}
