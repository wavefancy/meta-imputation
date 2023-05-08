package com.imputation.cromwell.jobs.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author fanshupeng
 * @create 2023/5/6 10:07
 */
@Getter
@Setter
public class JobsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 工作名称
     */
    private String jobName;

    /**
     * 工作流uuid
     */
    private String workflowExecutionUuid;

    /**
     * 消息uuid
     */
    private String messageUuid;

    /**
     * 参数集
     */
    private String jobJson;

    /**
     * 运行状态 3:Finish, 2:error ,1:In progress,0:Not started
     */
    private Integer status;
    /**
     * 执行wdl的脚本地址
     */
    private String wdlPath;
}
