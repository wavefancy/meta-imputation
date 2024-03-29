package com.imputation.jobs.running.dto;

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
     * 主键
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

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

    private String createdUser;

    private LocalDateTime createdDate;

    private String modifiedUser;

    private LocalDateTime modifiedDate;

    /**
     * 0：未删除，1：已删除
    */
    private Integer isDelete;
}
