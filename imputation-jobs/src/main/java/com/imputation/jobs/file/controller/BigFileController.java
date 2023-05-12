package com.imputation.jobs.file.controller;

import com.alibaba.fastjson.JSON;
import com.imputation.jobs.commons.JsonResult;
import com.imputation.jobs.constant.MessageEnum;
import com.imputation.jobs.file.dto.FileChunkReqDTO;
import com.imputation.jobs.file.dto.FileChunkResDTO;
import com.imputation.jobs.file.service.BigFileService;
import com.imputation.jobs.file.service.FileChunkService;
import com.imputation.jobs.utils.FileUtil;
import com.imputation.jobs.utils.MultipartFileToFileUtil;
import com.imputation.jobs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanshupeng
 * @create 2022/8/8 15:54
 */
@Slf4j
@RestController
@RequestMapping(value = "/imputation/job")
public class BigFileController {
    @Autowired
    private BigFileService bigFileService;
    @Autowired
    private FileChunkService fileChunkService;


    /**
     * 上传校验
     * @param param
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public JsonResult<Map<String, Object>> checkUpload(@Valid FileChunkReqDTO param) {
        log.info("文件MD5:" + param.getIdentifier());

        List<FileChunkResDTO> list = fileChunkService.listByFileMd5(param.getIdentifier());

        Map<String, Object> data = new HashMap<>(1);


        if (list.size() == 0) {
            data.put("uploaded", false);
            return JsonResult.ok(data);
        }

        // 处理单文件
        if (list.get(0).getTotalChunks() == 1) {
            data.put("uploaded", true);
            // todo 返回 url
            data.put("url", "");
            return JsonResult.ok(data);
        }
        // 处理分片
        int[] uploadedFiles = new int[list.size()];
        int index = 0;
        for (FileChunkResDTO fileChunkItem : list) {
            uploadedFiles[index] = fileChunkItem.getChunkNumber();
            index++;
        }
        data.put("uploadedChunks", uploadedFiles);
        return JsonResult.ok(data);
    }

    /**
     * 上传文件
     * @param param
     * @return
     */
    @PostMapping("/upload")
    public JsonResult<Map<String, Object>> chunkUpload( @Valid FileChunkReqDTO param, HttpServletRequest req) {
        //todo 修改测试值
        String email ="115110151301";
        log.info("上传文件email="+email);
        log.info("上传文件fileNameInput="+param.getFileNameInput());
        log.info("上传文件identifier="+param.getIdentifier());
        log.info("上传文件ChunkNumber="+param.getChunkNumber());

        if(StringUtils.isEmpty(param.getFileNameInput())){
            log.info("未录入文件名");
            return JsonResult.error(MessageEnum.FAIL);
        }
        Map<String,Object> resMap = bigFileService.uploadFile(email,param);
        Boolean flag = (Boolean)resMap.get("flag");
        if (!flag) {
            return JsonResult.error(MessageEnum.FAIL);
        }
        resMap.put("identifier",param.getIdentifier());
        return JsonResult.ok(resMap);
    }

}
