package com.imputation.jobs.file.service;


import com.imputation.jobs.file.dto.FileChunkReqDTO;
import com.imputation.jobs.file.dto.FileChunkResDTO;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/8/9 10:23
 */
public interface FileChunkService {
    /**
     * 根据文件 md5md5标识 查询
     *
     * @param identifier md5标识
     * @return
     */
    List<FileChunkResDTO> listByFileMd5(String identifier);

    /**
     * 保存记录
     *
     * @param param 记录参数
     */
    void saveFileChunk(FileChunkReqDTO param);
    /**
     * 删除分片记录
     * @param param
     * @return
     */
    Boolean deleteFileChunk(FileChunkReqDTO param);
}
