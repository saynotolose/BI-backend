package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.vo.BiResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author oykk
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-06-27 22:06:26
*/
public interface ChartService extends IService<Chart> {

    BiResponse geChartByAiAsyncMQ(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request);
}
