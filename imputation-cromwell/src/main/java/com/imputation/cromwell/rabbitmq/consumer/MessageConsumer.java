package com.imputation.cromwell.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imputation.cromwell.jobs.dto.JobsDTO;
import com.imputation.cromwell.jobs.service.JobsService;
import com.imputation.cromwell.rabbitmq.dto.MQMessageDTO;
import com.imputation.cromwell.rabbitmq.service.ProducerService;
import com.imputation.cromwell.utils.CromwellUtil;
import com.imputation.cromwell.utils.StringUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author fanshupeng
 * @create 2023/4/10 17:08
 */

@Component
@Slf4j
public class MessageConsumer {
    //imputation DIRECT交换机名称
    private static final String IMPUTATION_DIRECT_EXCHANGE_NAME = "imputation.direct.exchange";

    //imputation running submit队列名称
    public static final String IMPUTATION_RUNNING_SUBMIT_QUEUE_NAME = "imputation.running.submit.queue";
    private static final String IMPUTATION_RUNNING_SUBMIT_RES_ROUTING_KEY_NAME ="imputation.running.submit.res";

    // 查询未结束的工作流状态消息
    private static final String QUERY_JOBS_STATUS_QUEUE_NAME = "query.jobs.status.queue";
    // 查询未结束的工作流状态结果消息路由
    private static final String QUERY_JOBS_STATUS_RES_ROUTING_KEY_NAME ="query.jobs.status.res";

    @Autowired
    private ProducerService producerService;

    @Autowired
    private JobsService jobsService;

    /**
     * Cromwell服务访问地址
     */
    @Value("${cromwell.workflows.run.url}")
    private String cromwellUrl;

    /**
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues =IMPUTATION_RUNNING_SUBMIT_QUEUE_NAME)
    public void imputationRunningSubmitMsg(Message message, Channel channel) throws IOException {
        log.info("接受到imputation-job submit队列消息:{}", JSONObject.toJSONString(message));

        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);

        log.info("imputation-job submit队列消息msgJson:{}",msgJson);

        //提交工作流
        JobsDTO jobsDTO = toJobsDTO(msgJson);
        try {
            String cromwellId = jobsService.submitWorkflow(jobsDTO);

            //向前端发送工作流id消息
            JSONObject resMsg = new JSONObject();
            resMsg.put("cromwellId",cromwellId);
            resMsg.put("userName",jobsDTO.getUserName());
            resMsg.put("jobName",jobsDTO.getJobName());
            resMsg.put("messageIdOld",messageProperties.getMessageId());
            String messageId = UUID.randomUUID().toString();
            //当前系统时间
            LocalDateTime now = LocalDateTime.now();

            MQMessageDTO messageDTO = new MQMessageDTO();
            messageDTO.setMessage(resMsg.toString());
            messageDTO.setMsgId(messageId);
            messageDTO.setRoutingKey(IMPUTATION_RUNNING_SUBMIT_RES_ROUTING_KEY_NAME);
            messageDTO.setExchange(IMPUTATION_DIRECT_EXCHANGE_NAME);
            messageDTO.setTag("imputation.job");

            producerService.sendCromwellMessage(messageDTO);

            // 手动确认消息已经被消费
            channel.basicAck(messageProperties.getDeliveryTag(), false);

            log.info("已提交cromwell 向imputation-job发送消息:{}",JSONObject.toJSONString(messageDTO));

        }catch (Exception e){
            log.error("imputation-job提交cromwell出现异常:{}",e.getMessage());
            // 处理消息异常，将消息重新发送到死信队列中
            channel.basicNack(messageProperties.getDeliveryTag(), false, false);
        }

    }

    /**
     * 监听查询未结束的工作流状态消息
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues =QUERY_JOBS_STATUS_QUEUE_NAME)
    public void queryJobsStatusMsg(Message message, Channel channel) throws IOException {
        log.info("接受到查询未结束的工作流状态消息:{}", JSONObject.toJSONString(message));

        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);

        log.info("查询未结束的工作流状态消息msgJson:{}",msgJson);

        String uuidArr = (String) msgJson.get("uuidArr");
        try {
            JSONObject jsonObject = new JSONObject();
            //遍历uuid查询对应的工作流状态
            for (String uuidStr:uuidArr.split(",")) {
                String[] uuidStrArr = uuidStr.split("@");
                String uuid = uuidStrArr[0];
                String statusOld = uuidStrArr[1];
                String status = CromwellUtil.workflowsStatus(cromwellUrl,uuid);
                JSONObject resMsg = new JSONObject();
                resMsg.put("cromwellId",uuid);
                resMsg.put("status",status);
                resMsg.put("resPath","");
                jsonObject.put(uuid,resMsg);
            }

            //向前端发送工作流id消息
            String messageId = UUID.randomUUID().toString();
            //当前系统时间
            LocalDateTime now = LocalDateTime.now();
            MQMessageDTO messageDTO = new MQMessageDTO();
            messageDTO.setMessage(jsonObject.toString());
            messageDTO.setMsgId(messageId);
            messageDTO.setRoutingKey(QUERY_JOBS_STATUS_RES_ROUTING_KEY_NAME);
            messageDTO.setExchange(IMPUTATION_DIRECT_EXCHANGE_NAME);
            messageDTO.setTag("imputation.job");

            producerService.sendCromwellMessage(messageDTO);

            // 手动确认消息已经被消费
            channel.basicAck(messageProperties.getDeliveryTag(), false);

            log.info("已提交cromwell 向imputation-job发送消息:{}",JSONObject.toJSONString(messageDTO));

        }catch (Exception e){
            log.error("imputation-job提交cromwell出现异常:{}",e.getMessage());
            // 处理消息异常，将消息重新发送到死信队列中
            channel.basicNack(messageProperties.getDeliveryTag(), false, false);
        }

    }

    private JobsDTO toJobsDTO(JSONObject msgJson) {
        JobsDTO jobsDTO = new JobsDTO();
        String userName = (String) msgJson.get("userName");
        if(StringUtils.isNotEmpty(userName)){
            jobsDTO.setUserName(userName);
        }
        String jobName = (String) msgJson.get("jobName");
        if(StringUtils.isNotEmpty(jobName)){
            jobsDTO.setJobName(jobName);
        }
        String jobJson = (String) msgJson.get("jobJson");
        if(StringUtils.isNotEmpty(jobJson)){
            jobsDTO.setJobJson(jobJson);
        }
        return jobsDTO;
    }


}
