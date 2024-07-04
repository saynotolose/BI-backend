package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    void doRateLimit() throws InterruptedException {
        //模拟id
        String userId = "1111";

        for (int i = 0; i < 2; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("调用成功");
        }

        //Thread.sleep(10000);
        //
        //for (int i = 0; i < 5; i++) {
        //    redisLimiterManager.doRateLimit(userId);
        //    System.out.println("调用成功");
        //}
    }
}