package com.imputation.jobs.file.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fanshupeng
 * @create 2022/8/9 11:08
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FileChunkReqDTO {


    private Long id;
    //是否为新增数据
    public boolean isNew() {
        return null == this.id;
    }

    @NotNull(message = "当前分片不能为空")
    private Integer chunkNumber;

    @NotNull(message = "分片大小不能为空")
    private Float chunkSize;

    @NotNull(message = "当前分片大小不能为空")
    private Float currentChunkSize;

    @NotNull(message = "文件总数不能为空")
    private Integer totalChunks;

    @NotBlank(message = "文件标识不能为空")
    private String identifier;

    @NotBlank(message = "文件名不能为空")
    private String filename;
    /**
     * 用户输入的文件名
     */
    private String fileNameInput;

    private String fileType;

    private String relativePath;

    @NotNull(message = "文件总大小不能为空")
    private Float totalSize;

    private MultipartFile file;
}
