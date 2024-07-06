package com.yupi.springbootinit.config;


import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置类
 */
@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    //创建线程池，返回一个线程池
    public ThreadPoolExecutor threadPoolExecutor(){

        //创建线程工厂
        ThreadFactory threadFactory = new ThreadFactory() {
            //初始化线程数为 1
            private int count = 1;
            @Override
            // 每当线程池需要创建新线程时，就会调用newThread方法
            // @NotNull Runnable r 表示方法接收的参数 r 不允许为null，否则报错
            public Thread newThread(@NotNull Runnable r) {
                //创建一个新线程，设置名称，用当前线程数标识是第几个线程
                Thread thread = new Thread(r);
                thread.setName("线程" + count);
                //线程数递增
                count++;
                return thread;
            }
        };

        //创建线程池，线程池核心大小为2，最大线程数为4，非核心线程空闲100s回收资源，
        //阻塞队列长度为 4, 使用自定义的线程工厂创建线程
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4,
                100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4), threadFactory);

        //返回创建的线程池
        return threadPoolExecutor;

    }

}
