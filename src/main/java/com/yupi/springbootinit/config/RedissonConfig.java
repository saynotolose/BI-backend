package com.yupi.springbootinit.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson 限流器配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {

    private Integer database;
    private String host;
    private String password;
    private String port;

    //项目启动时，会自动创建一个RedissonClient对象
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer()        //添加单机Redisson配置
                .setDatabase(database)
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }



}
