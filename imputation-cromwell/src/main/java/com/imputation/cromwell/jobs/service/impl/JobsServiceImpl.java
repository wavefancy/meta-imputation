package com.imputation.cromwell.jobs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imputation.cromwell.jobs.dto.JobsDTO;
import com.imputation.cromwell.jobs.service.JobsService;
import com.imputation.cromwell.utils.FileUtil;
import com.imputation.cromwell.utils.HttpClientUtil;
import com.imputation.cromwell.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanshupeng
 * @create 2023/5/6 17:07
 */
@Slf4j
@Service
public class JobsServiceImpl implements JobsService {
    /**
     * Cromwell服务访问地址
     */
    @Value("${cromwell.workflows.run.url}")
    private String cromwellUrl;
    @Value("${imputation.wdl.path}")
    private String wdlPath;
    @Value("${temporary.file.path}")
    private String temporaryFilePath;
    @Override
    public String submitWorkflow(JobsDTO jobsDTO) throws Exception {
        log.info("提交工作流开始algorithmsParameterDTO="+ JSON.toJSONString(jobsDTO));

        //参数临时文件地址
        String inputFilePath = temporaryFilePath+System.currentTimeMillis()+"_inputFile.json";
        String jobJson = jobsDTO.getJobJson();
        JSONObject inputJson = JSONObject.parseObject(jobJson);
        log.info("将参数写入临时文件地址中inputFilePath="+inputFilePath);
        FileUtil.writerJsonFile(inputFilePath,inputJson);

        File inputFile = new File(inputFilePath);
        File wdlFile = new File(wdlPath);

        Map<String,File> fileMap = new HashMap<>();
        fileMap.put("workflowInputs",inputFile);
        fileMap.put("workflowSource",wdlFile);

        log.info("访问cromwell提交工作流");
        String resultmsg = HttpClientUtil.httpClientUploadFileByfile(fileMap,cromwellUrl);
        log.info("访问cromwell提交工作流返回结果"+JSON.toJSONString(resultmsg));
        String cromwellId = null;
        if(StringUtils.isNotEmpty(resultmsg)){
            JSONObject  cromwellResult = JSON.parseObject(resultmsg);
            cromwellId = cromwellResult.get("id").toString();
        }
        //删除临时文件
        FileUtil.delAllFile(inputFilePath);
        log.info("提交工作流结束 cromwellId="+ cromwellId);

        return cromwellId;
    }
}
