package com.imputation.jobs.table.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.imputation.jobs.practice.bo.JobsBo;
import com.imputation.jobs.practice.entity.Jobs;
import com.imputation.jobs.rabbitmq.dto.MQMessageDTO;
import com.imputation.jobs.rabbitmq.service.ProducerService;
import com.imputation.jobs.table.service.JobsTableService;
import com.imputation.jobs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author fanshupeng
 * @create 2023/5/9 16:53
 */
@Slf4j
@Service
public class JobsTableServiceImpl implements JobsTableService {
    //imputation DIRECT交换机名称
    private static final String IMPUTATION_DIRECT_EXCHANGE_NAME = "imputation.direct.exchange";
    // 查询未结束的工作流状态消息
    private static final String QUERY_JOBS_STATUS_ROUTING_KEY_NAME = "query.jobs.status.routing.key";

    @Autowired
    private JobsBo jobsBo;
    /**
     * 发送消息
     */
    @Autowired
    private ProducerService producerService;
    /**
     * 定时任务：实时查询Jobs数据 发送查询未结束的工作流消息
     */
    @Scheduled(cron = "0 60 * * * ?")
    private void realTimeUpdateRunnerDetail(){
        log.info("实时查询Jobs数据定时任务开始每60秒执行一次");

        QueryWrapper<Jobs> jobsQueryWrapper = new QueryWrapper<>();
        List<Integer> statusList = new ArrayList<>();
        statusList.add(0);
        statusList.add(1);
        jobsQueryWrapper.in("status",statusList);
        log.info("查询jobs表未结束的工作流入参jobsQueryWrapper="+ JSON.toJSONString(jobsQueryWrapper));
        List<Jobs> jobsList = jobsBo.list(jobsQueryWrapper);
        log.info("查询jobs表未结束的工作流出参jobsList="+ JSON.toJSONString(jobsList));

        if(CollectionUtils.isNotEmpty(jobsList)){
            StringBuffer weUuidStrBuffer = new StringBuffer();
            for (Jobs jobs:jobsList) {
                String weUuid = jobs.getWorkflowExecutionUuid();
                if(StringUtils.isEmpty(weUuid)){
                    continue;
                }
                Integer status = jobs.getStatus();
                if(weUuidStrBuffer.length() == 0){
                    weUuidStrBuffer.append(weUuid+"@"+status);
                }else{
                    weUuidStrBuffer.append(","+weUuid+"@"+status);
                }
            }
            if(weUuidStrBuffer.length()>0){
                //发送消息查询工作流状态
                JSONObject reqMap = new JSONObject();
                reqMap.put("uuidArr",weUuidStrBuffer.toString());
                this.sendQueryJobsStatusMessage(reqMap);
            }
        }

    }
    /**
     * 发送消息
     * @param reqMap
     */
    private Boolean sendQueryJobsStatusMessage(Map<String, Object> reqMap) {
        log.info("查询工作流状态变更 调用发送消息入参：{}", JSONObject.toJSONString(reqMap));

        String messageId = UUID.randomUUID().toString();

        JSONObject inputJson = new JSONObject(reqMap);

        MQMessageDTO messageDTO = new MQMessageDTO();
        messageDTO.setMessage(inputJson.toJSONString());
        messageDTO.setMsgId(messageId);
        messageDTO.setExchange(IMPUTATION_DIRECT_EXCHANGE_NAME);
        messageDTO.setRoutingKey(QUERY_JOBS_STATUS_ROUTING_KEY_NAME);
        messageDTO.setTag("imputation.job");

        return producerService.sendCromwellMessage(messageDTO);
    }
}
