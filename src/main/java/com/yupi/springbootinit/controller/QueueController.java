package com.yupi.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池执行流程测试
 */

@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev", "local"})
public class QueueController {

    @Resource
    //自动注入一个线程池实例
    ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name){
        //使用CompletableFuture执行一个异步任务
        CompletableFuture.runAsync(() -> {
            log.info("任务执行中：" + name + "，执行人：" + Thread.currentThread().getName());

            try {
                //线程休眠10分钟，模拟耗时长的任务
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //异步任务在threadPoolExecutor线程池中运行
        }, threadPoolExecutor);
    }


    @GetMapping("/get")
    public String get() {
        Map<String,Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度", size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("任务总数", taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成任务数", completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("正在工作的线程数",activeCount);
        //将Object对象转为Json字符串
        return JSONUtil.toJsonStr(map);
    }

}
