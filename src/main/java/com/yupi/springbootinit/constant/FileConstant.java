package com.yupi.springbootinit.constant;

import org.checkerframework.checker.units.qual.A;

import java.util.Arrays;
import java.util.List;

/**
 * 文件常量  文件校验相关
 *
 *
 */
public interface FileConstant {

    /**
     * COS 访问地址
     * todo 需替换配置
     */
    String COS_HOST = "https://yupi.icu";

    /**
     * 定义一个常量表示 1MB 的大小
     */
    Long ONE_MB = 1024*1024L;

    /**
     * 定义合法的文件后缀列表
     */
    List<String> VALID_FILE_SUFFIX_LIST = Arrays.asList("png", "xlsx", "xls", "jpg", "svg", "webp", "jpeg");


}
