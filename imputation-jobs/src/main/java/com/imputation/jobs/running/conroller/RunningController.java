package com.imputation.jobs.running.conroller;

import com.alibaba.fastjson.JSONObject;
import com.imputation.jobs.commons.BaseResult;
import com.imputation.jobs.rabbitmq.dto.MQMessageDTO;
import com.imputation.jobs.rabbitmq.service.ProducerService;
import com.imputation.jobs.running.dto.JobReqDTO;
import com.imputation.jobs.running.dto.JobsDTO;
import com.imputation.jobs.running.service.JobsService;
import com.imputation.jobs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author fanshupeng
 * @create 2023/4/26 16:50
 */
@Slf4j
@RestController
@RequestMapping(value = "/imputation/job")
public class RunningController {

    //imputation DIRECT交换机名称
    private static final String IMPUTATION_DIRECT_EXCHANGE_NAME = "imputation.direct.exchange";
    private static final String IMPUTATION_JOB_SUBMIT_ROUTING_KEY = "imputation.job.submit.result";

    @Autowired
    private JobsService jobsService;

    @Autowired
    private ProducerService producerService;

    /**
     * job-submit
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public BaseResult jobSubmit(JobReqDTO jobReqDTO){

        //job数据落库
        JobsDTO jobsDTO = toJobsDTO(jobReqDTO);
        jobsService.saveOrUpdateJob(jobsDTO);

        JSONObject jsonObject = JSONObject.parseObject(jobReqDTO.getJobJson());
        String[] strs = {"21", "22"};
        jsonObject.put("Imputation.contigs",strs);
        //发送消息到Cromwell
        JSONObject  resMsg = new JSONObject();
        resMsg.put("userName",jobReqDTO.getUserName());
        resMsg.put("jobJson",jsonObject.toJSONString());
        resMsg.put("jobName",jobReqDTO.getJobName());
        String messageId = UUID.randomUUID().toString();
        //当前系统时间
        LocalDateTime now = LocalDateTime.now();

        MQMessageDTO messageDTO = new MQMessageDTO();
        messageDTO.setMessage(resMsg.toString());
        messageDTO.setMsgId(messageId);
        messageDTO.setRoutingKey(IMPUTATION_JOB_SUBMIT_ROUTING_KEY);
        messageDTO.setExchange(IMPUTATION_DIRECT_EXCHANGE_NAME);
        messageDTO.setTag("prs.hub");

        producerService.sendCromwellMessage(messageDTO);

        return null;
    }

    private JobsDTO toJobsDTO(JobReqDTO jobReqDTO) {
        JobsDTO jobsDTO = new JobsDTO();
        if(jobReqDTO == null){
            return jobsDTO;
        }
        String jobJson = jobReqDTO.getJobJson();
        if(StringUtils.isNotEmpty(jobJson)){
            jobsDTO.setJobJson(jobJson);
        }
        String jobName = jobReqDTO.getJobName();
        if(StringUtils.isNotEmpty(jobName)){
            jobsDTO.setJobName(jobName);
        }
        String userName = jobReqDTO.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            jobsDTO.setUserName(userName);
        }

        jobsDTO.setStatus(0);

        //当前系统时间
        LocalDateTime now = LocalDateTime.now();

        jobsDTO.setCreatedUser("system");
        jobsDTO.setCreatedDate(now);
        jobsDTO.setModifiedUser("system");
        jobsDTO.setModifiedDate(now);
        jobsDTO.setIsDelete(0);

        return jobsDTO;
    }

}
