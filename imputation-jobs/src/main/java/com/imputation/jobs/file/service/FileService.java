package com.imputation.jobs.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imputation.jobs.file.dto.FileReqDTO;
import com.imputation.jobs.practice.entity.Files;

public interface FileService {
    /**
     * 新增文件的信息
     * @param files 文件信息
     * @return
     */
    Long saveFileDetail(Files files) throws Exception;
    IPage<Files> getFileList(FileReqDTO fileReqDTO)throws Exception;

}
