package com.yupi.springbootinit.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 *
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * 要删除的图表id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}