package com.yupi.springbootinit.model.vo;


import lombok.Data;

/**
 * 封装AI分析的返回结果
 */
@Data
public class BiResponse {
    /**
     * 生成图表的数据内容
     */
    private String genChart;
    /**
     * 生成图表的文字分析结果
     */
    private String genResult;
    /**
     * 图表 id
     */
    private Long chartId;
}
