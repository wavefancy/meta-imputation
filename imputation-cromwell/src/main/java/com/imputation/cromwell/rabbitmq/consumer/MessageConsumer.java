package com.imputation.cromwell.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imputation.cromwell.jobs.dto.JobsDTO;
import com.imputation.cromwell.jobs.service.JobsService;
import com.imputation.cromwell.rabbitmq.dto.MQMessageDTO;
import com.imputation.cromwell.rabbitmq.service.ProducerService;
import com.imputation.cromwell.utils.CromwellUtil;
import com.imputation.cromwell.utils.ProcessUtils;
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

    //文件上传队列名称
    private static final String IMPUTATION_UPLOAD_BEIJING_QUEUE_NAME = "imputation.upload.file.queue.beijing" ;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private JobsService jobsService;

    @Autowired
    private ProcessUtils processUtils;

    /**
     * Cromwell服务访问地址
     */
    @Value("${cromwell.workflows.run.url}")
    private String cromwellUrl;
    /**
     * 查询对应uuid的工作流状态地址
     */
    @Value("${cromwell.workflows.status.url}")
    private String cromwellStatusUrl;

    /**
     * 目标地址
     */
    @Value("${rsync.destination.path.beijing}")
    private  String destinationPath;

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

            if(StringUtils.isNotEmpty(cromwellId)){
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
                log.info("已提交cromwell 向imputation-job发送消息:{}",JSONObject.toJSONString(messageDTO));
            }

            // 手动确认消息已经被消费
            channel.basicAck(messageProperties.getDeliveryTag(), false);
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
                String status = CromwellUtil.workflowsStatus(cromwellStatusUrl,uuid);
                if(StringUtils.isNotEmpty(status)){
                    JSONObject resMsg = new JSONObject();
                    resMsg.put("cromwellId",uuid);
                    resMsg.put("status",status);
                    resMsg.put("resPath","");
                    jsonObject.put(uuid,resMsg);
                }
            }

            if(jsonObject.size()>0){
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

                log.info("已提交cromwell 向imputation-job发送消息:{}",JSONObject.toJSONString(messageDTO));
            }

            // 手动确认消息已经被消费
            channel.basicAck(messageProperties.getDeliveryTag(), false);


        }catch (Exception e){
            log.error("imputation-job提交cromwell出现异常:{}",e.getMessage());
            // 处理消息异常，将消息重新发送到死信队列中
            channel.basicNack(messageProperties.getDeliveryTag(), false, false);
        }

    }

    /**
     * 监听文件上传消息
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues =IMPUTATION_UPLOAD_BEIJING_QUEUE_NAME)
    public void uploadFileMsg(Message message, Channel channel) throws IOException {
        log.info("接受到文件上传消息:{}", JSONObject.toJSONString(message));

        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);
        String fileId = (String)msgJson.get("fileId");
        String filePath = (String)msgJson.get("filePath");
        String fileName = (String)msgJson.get("fileName");
        String suffixName = (String)msgJson.get("suffixName");
        String userId = (String)msgJson.get("userId");
        try {
            //复制文件

            //拼装源文件地址
            String sourcePath = filePath+fileName+suffixName;
            //目标文件名
            String destinationFileName = fileName + "_" + userId + "_" + fileId + suffixName;
            boolean rsyncFlag = processUtils.rsyncPull(sourcePath,destinationFileName,destinationPath);

            Map<String,Object> resMsg = new HashMap<>();
            resMsg.put("rsyncFlag",rsyncFlag);
            if (rsyncFlag){
                //发送文件同步完成消息
//                resMsg.put("fileId",fileId);
//                String messageId = UUID.randomUUID().toString();
//                MQMessageDTO messageDTO = new MQMessageDTO();
//                messageDTO.setMessage(resMsg.toString());
//                messageDTO.setMsgId(messageId);
//                messageDTO.setRoutingKey(fileSyncResRoutingKey);
//                messageDTO.setExchange(PRS_HUB_UPLOAD_EXCHANGE_NAME);
//                messageDTO.setTag("prs.hub");
//
//                producerService.sendCromwellMessage(messageDTO);
                // 手动确认algorithmsParameter消息已经被消费
                channel.basicAck(messageProperties.getDeliveryTag(), false);
            }else {
                // 同步文件出现问题，将消息重新发送到死信队列中
                channel.basicNack(messageProperties.getDeliveryTag(), false, false);
            }

        }catch (Exception e){
            log.error("同步文件异常：{}",e.getMessage());
            // 同步文件出现问题，将消息重新发送到死信队列中
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
