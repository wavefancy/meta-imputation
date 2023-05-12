package com.imputation.jobs.file.service;


import com.imputation.jobs.file.dto.ChunkDTO;
import com.imputation.jobs.file.dto.FileChunkReqDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author fanshupeng
 * @create 2022/8/8 15:56
 */
public interface BigFileService {
    /**
     *
     * @param chunk
     * @param response
     * @return
     */
    String fileUploadPost(ChunkDTO chunk, HttpServletResponse response);
    /**
     * 上传文件
     * @param param 参数
     * @return
     */
    Map<String,Object> uploadFile(String email, FileChunkReqDTO param);
}
