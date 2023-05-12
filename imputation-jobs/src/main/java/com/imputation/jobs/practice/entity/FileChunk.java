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
 * 分片表
 * </p>
 *
 * @author fansp
 * @since 2023-05-11
 */
@Getter
@Setter
@TableName("file_chunk")
@ApiModel(value = "FileChunk对象", description = "分片表")
public class FileChunk implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    @TableField("version")
    private Boolean version;

    @TableField("status")
    private Boolean status;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("add_time")
    private LocalDateTime addTime;

    @ApiModelProperty("当前分片，从1开始")
    @TableField("chunk_number")
    private Integer chunkNumber;

    @ApiModelProperty("分片大小")
    @TableField("chunk_size")
    private Float chunkSize;

    @ApiModelProperty("当前分片大小")
    @TableField("current_chunk_size")
    private Float currentChunkSize;

    @ApiModelProperty("总分片数")
    @TableField("total_chunks")
    private Integer totalChunks;

    @ApiModelProperty("文件标识")
    @TableField("identifier")
    private String identifier;

    @ApiModelProperty("文件名")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty("文件类型")
    @TableField("file_type")
    private String fileType;

    @TableField("relative_path")
    private String relativePath;


}
