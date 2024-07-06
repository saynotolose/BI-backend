package com.yupi.springbootinit.constant;

/**
 * 生成图表任务状态常量
 */
public interface ChartStatus {

    /**
     * 等待中，还没开始生成
     */
    String WAIT = "wait";

    /**
     * 正在生成
     */
    String RUNNING = "running";

    /**
     * 成功生成完成
     */
    String SUCCEED = "succeed";

    /**
     * 生成失败
     */
    String FAILED = "failed";
}
