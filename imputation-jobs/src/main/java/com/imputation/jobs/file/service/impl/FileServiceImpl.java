package com.imputation.jobs.file.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imputation.jobs.file.dto.FileReqDTO;
import com.imputation.jobs.file.service.FileService;
import com.imputation.jobs.practice.bo.FilesBo;
import com.imputation.jobs.practice.entity.Files;
import com.imputation.jobs.rabbitmq.dto.MQMessageDTO;
import com.imputation.jobs.rabbitmq.service.ProducerService;
import com.imputation.jobs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@EnableScheduling
public class FileServiceImpl implements FileService {

    @Autowired
    private FilesBo filesBo;
    /**
     * 新增文件的信息
     * @param files 文件信息
     * @return
     */
    @Override
    public Long saveFileDetail(Files files) throws Exception{
        log.info("新增文件的信息files="+JSON.toJSONString(files));

        Boolean flag = false;
        //将这些文件的信息写入到数据库中
        LocalDateTime now = LocalDateTime.now();
        files.setCreatedUser("system");
        files.setCreatedDate(now);
        files.setModifiedUser("system");
        files.setModifiedDate(now);
        files.setDeleteDate(now.plusDays(30));
        files.setIsDelete(0);

        log.info("调用bo新增文件的信息开始files="+JSON.toJSON(files));
        flag = filesBo.save(files);
        log.info("调用bo新增文件的信息结束flag="+flag);

        Long resInt = null;
        if(flag){
            QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",files.getUserId());
            queryWrapper.eq("file_path",files.getFilePath());
            queryWrapper.eq("file_name",files.getFileName());
            queryWrapper.eq("file_suffix",files.getFileSuffix());
            log.info("调用bo查询新增文件的信息开始queryWrapper="+JSON.toJSON(queryWrapper));
            Files fileRes = filesBo.getOne(queryWrapper);
            log.info("调用bo查询新增文件的信息结束fileRes="+JSON.toJSONString(fileRes));
            if(fileRes != null){
                resInt = fileRes.getId();
            }
        }
        return resInt;
    }
    @Override
    public IPage<Files> getFileList(FileReqDTO fileReqDTO) throws Exception {
        log.info("文件service根据fileReqDTO分页查询文件信息,fileReqDTO="+JSON.toJSONString(fileReqDTO));
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        if(fileReqDTO.getId()!=null){
            queryWrapper.eq("id",fileReqDTO.getId());
        }
        if(fileReqDTO.getUserId()!=null){
            queryWrapper.eq("user_id",fileReqDTO.getUserId());
        }
        queryWrapper.orderByDesc("created_date");
        IPage<Files> iPage = new Page<>();
        iPage.setCurrent(fileReqDTO.getCurrent());
        iPage.setSize(fileReqDTO.getSize());
        log.info("调用bo分页查询文件的信息开始queryWrapper="+JSON.toJSON(queryWrapper));
        IPage<Files> filesIPage = filesBo.page(iPage,queryWrapper);
        log.info("调用bo分页查询文件的信息结束filesIPage="+JSON.toJSON(filesIPage));

        return filesIPage;
    }
}
