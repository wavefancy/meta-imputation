package com.imputation.jobs.running.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author fanshupeng
 * @create 2023/4/26 16:51
 */
@Getter
@Setter
public class JobReqDTO  implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 工作流名称
     */
    private String jobName;
    /**
     * 工作流参数集
     */
    private String jobJson;
}
