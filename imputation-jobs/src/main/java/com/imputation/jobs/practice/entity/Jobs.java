package com.imputation.jobs.practice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 工作流数据表
 * </p>
 *
 * @author fansp
 * @since 2023-05-11
 */
@Getter
@Setter
@TableName("jobs")
@ApiModel(value = "Jobs对象", description = "工作流数据表")
public class Jobs implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("工作名称")
    @TableField("job_name")
    private String jobName;

    @ApiModelProperty("工作流uuid")
    @TableField("workflow_execution_uuid")
    private String workflowExecutionUuid;

    @ApiModelProperty("消息uuid")
    @TableField("message_uuid")
    private String messageUuid;

    @ApiModelProperty("参数集")
    @TableField("job_json")
    private String jobJson;

    @ApiModelProperty("运行状态 3:Finish, 2:error ,1:In progress,0:Not started")
    @TableField("status")
    private Integer status;

    @TableField("created_user")
    private String createdUser;

    @TableField("created_date")
    private LocalDateTime createdDate;

    @TableField("modified_user")
    private String modifiedUser;

    @TableField("modified_date")
    private LocalDateTime modifiedDate;

    @ApiModelProperty("0：未删除，1：已删除")
    @TableField("is_delete")
    private Integer isDelete;


}
