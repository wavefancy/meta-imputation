package com.imputation.jobs.file.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author fanshupeng
 * @create 2022/4/14 13:55
 */
@Getter
@Setter
public class FileResDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键**/
    private Long id;

    /**用户id**/
    private Long userId;
    /**文件格式解析工作流id**/
    private String parsingId;

    /**文件格式解析工作流状态：Y:完成、N:未完成**/
    private String parsingStatus;
    /**
     * 文件标识
     */
    private String identifier;
    /**路径**/
    private String filePath;

    /**文件名**/
    private String fileName;

    /**后缀**/
    private String fileSuffix;

    /**文件类型**/
    private String fileType;

    /**描述**/
    private String descrition;
    /**
     * 状态
     */
    private String status;
    /**
     * 上传日期
     */
    private String uploadDate;
    /**
     * 失效日期
     */
    private String deleteDate;
}
