package com.imputation.jobs.file.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imputation.jobs.commons.BaseResult;
import com.imputation.jobs.constant.ResultCodeEnum;
import com.imputation.jobs.file.dto.FileReqDTO;
import com.imputation.jobs.file.service.FileChunkService;
import com.imputation.jobs.file.service.FileService;
import com.imputation.jobs.practice.entity.Files;
import com.imputation.jobs.rabbitmq.dto.MQMessageDTO;
import com.imputation.jobs.rabbitmq.service.ProducerService;
import com.imputation.jobs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/imputation/job")
public class FileController {
    @Autowired
    private FileService fileService;


    @Autowired
    private FileChunkService fileChunkService;

    /**
     * 发送消息
     */
    @Autowired
    private ProducerService producerService;

    @Value("${upload.exchange}")
    private String uploadExchange;

    @Value("${upload.file.routing.key}")
    private String uploadRoutingKey;


    /**
     * 获取文件信息
     */
    @RequestMapping(value = "/getFileList",method = RequestMethod.GET)
    public BaseResult getFileList(HttpServletRequest req, FileReqDTO fileReqDTO){
        log.info("获取文件信息Controller开始fileReqDTO="+JSON.toJSONString(fileReqDTO));
        Map<String,Object> resultMap = new HashMap<>();
        try {
            fileReqDTO.setUserId(Long.valueOf(1));
            log.info("调用service分页查询文件信息开始fileReqDTO="+JSON.toJSONString(fileReqDTO));
            IPage<Files> filesIPage = fileService.getFileList(fileReqDTO);
            log.info("调用service分页查询文件信息结束filesIPage="+JSON.toJSONString(filesIPage));

            resultMap.put("total",filesIPage.getTotal());
            resultMap.put("current",filesIPage.getCurrent());
            resultMap.put("resDTOList",filesIPage.getRecords());
            resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("msg","获取文件信息成功");
        }catch (Exception e){
            log.error("获取文件信息controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","获取文件信息异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);
    }


    /**
     * 保存上传文件信息
     * @param req
     * @return
     */
    @RequestMapping(value = "/saveFilesInfo",method = RequestMethod.GET)
    public BaseResult saveFilesInfo(HttpServletRequest req ){
        Map<String,Object> resultMap = new HashMap<>();
        //文件名
        String fileName = req.getParameter("fileName");
        //文件描述
        String descrition = req.getParameter("descrition");
        //存储路径
        String filePath = req.getParameter("filePath");
        //文件后缀
        String suffixName = req.getParameter("suffixName");
        //文件分片存储标识
        String identifier = req.getParameter("identifier");

        log.info("保存上传文件信息controller:\nfileName="+fileName+"\ndescrition="+descrition+"\nfilePath="+filePath+"\nsuffixName="+suffixName);
        if(StringUtils.isEmpty(fileName) || StringUtils.isEmpty(filePath)){
            resultMap.put("code", ResultCodeEnum.FILE_NAME_EMPTY.getCode());
            resultMap.put("msg",ResultCodeEnum.FILE_NAME_EMPTY.getName());
            return BaseResult.ok("文件上传接口调用成功",resultMap);
        }

        try {
            Files files = new Files();
            files.setDescrition(descrition);
            files.setFilePath(filePath);
            files.setFileName(fileName);
            files.setFileSuffix(suffixName);
            files.setIdentifier(identifier);
            //TODO 现在写死将来改
            files.setUserId(Long.valueOf(1));
            log.info("调用fileService将上传文件信息存储到数据库开始");
            Long fileId = fileService.saveFileDetail(files);
            log.info("调用fileService将上传文件信息存储到数据库结束fileId="+fileId);
            if(fileId !=null){
                resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
                resultMap.put("msg","sftp文件上传成功");
                resultMap.put("fileId",fileId);
                //发送消息
                JSONObject inputJson = new JSONObject();
                inputJson.put("fileId",fileId.toString());
                inputJson.put("filePath",filePath);
                inputJson.put("fileName",fileName);
                inputJson.put("suffixName",suffixName);
                inputJson.put("userId","1");
                this.sendUploadMessage(inputJson);
            }else {
                resultMap.put("code",ResultCodeEnum.FAIL.getCode());
                resultMap.put("msg","sftp文件上传失败");
            }



        }catch (Exception e){
            log.error("文件上传controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","文件上传异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);

    }

    /**
     * 发送消息
     * @param inputJson
     */
    private Boolean sendUploadMessage(JSONObject inputJson) {
        log.info("文件上传保存数据后调用发送消息入参：{}",inputJson.toJSONString());

        String messageId = UUID.randomUUID().toString();

        MQMessageDTO messageDTO = new MQMessageDTO();
        messageDTO.setMessage(inputJson.toJSONString());
        messageDTO.setMsgId(messageId);
        messageDTO.setRoutingKey(uploadRoutingKey);
        messageDTO.setTag(uploadRoutingKey);

        messageDTO.setExchange(uploadExchange);
        return producerService.sendCromwellMessage(messageDTO);
    }
}
