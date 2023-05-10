package com.imputation.jobs.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imputation.jobs.running.dto.JobsDTO;
import com.imputation.jobs.running.service.JobsService;
import com.imputation.jobs.utils.StringUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author fanshupeng
 * @create 2023/4/10 17:08
 */

@Component
@Slf4j
public class MessageConsumer {
    //imputation DIRECT交换机名称
    private static final String IMPUTATION_DIRECT_EXCHANGE_NAME = "imputation.direct.exchange";

    //imputation running submit 结果返回队列名称
    public static final String IMPUTATION_RUNNING_SUBMIT_RES_QUEUE_NAME = "imputation.running.submit.res.queue";
    // 查询未结束的工作流状态结果消息队列
    private static final String QUERY_JOBS_STATUS_RES_QUEUE_NAME ="query.jobs.status.res.queue";

    @Autowired
    private JobsService jobsService;

    /**
     * 监听工作流提交成功返回队列
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues =IMPUTATION_RUNNING_SUBMIT_RES_QUEUE_NAME)
    public void imputationRunningSubmitMsg(Message message, Channel channel) throws IOException {
        log.info("接受到imputation running submit res队列消息:{}", JSONObject.toJSONString(message));

        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);
        JobsDTO jobsDTO = toJobsDTO(msgJson);
        try {
            if(StringUtils.isNotEmpty(jobsDTO.getUserName())
                    &&StringUtils.isNotEmpty(jobsDTO.getJobName())){
                jobsService.saveOrUpdateJob(jobsDTO);
            }else{
                //
            }

            // 手动确认消息已经被消费
            channel.basicAck(messageProperties.getDeliveryTag(), false);
        }catch (Exception e){
            log.error("imputation running submit res队列消息出现异常:{}",e.getMessage());
            // 处理消息异常，将消息重新发送到死信队列中
            channel.basicNack(messageProperties.getDeliveryTag(), false, false);

        }
    }

    /**
     * 监听查询未结束的工作流状态结果消息队列
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues =QUERY_JOBS_STATUS_RES_QUEUE_NAME)
    public void  queryJobsStatusResQueueMsg(Message message, Channel channel) throws IOException {
        log.info("接受到监听查询未结束的工作流状态结果消息队列:{}", JSONObject.toJSONString(message));

        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);
        log.info("查询未结束的工作流状态消息msgJson:{}",msgJson);
        try {
            for (Map.Entry<String, Object> stringObjectEntry : msgJson.entrySet()) {
                String uuid = stringObjectEntry.getKey();
                JSONObject resMap=  (JSONObject) stringObjectEntry.getValue() ;
                String cromwellId = (String)resMap.get("cromwellId");
                String status = (String)resMap.get("status");
                String resPath = (String)resMap.get("resPath");
                JobsDTO jobsDTO = new JobsDTO();
                jobsDTO.setStatus(0);
                jobsDTO.setWorkflowExecutionUuid(cromwellId);
                jobsService.saveOrUpdateJob(jobsDTO);
            }
            // 手动确认消息已经被消费
            channel.basicAck(messageProperties.getDeliveryTag(), false);
        }catch (Exception e){
            log.error("监听查询未结束的工作流状态结果消息队列出现异常:{}",e.getMessage());
            // 处理消息异常，将消息重新发送到死信队列中
            channel.basicNack(messageProperties.getDeliveryTag(), false, false);

        }
    }

    private JobsDTO toJobsDTO(JSONObject msgJson) {
        JobsDTO jobsDTO = new JobsDTO();
        String cromwellId = (String) msgJson.get("cromwellId");
        if(StringUtils.isNotEmpty(cromwellId)){
            jobsDTO.setWorkflowExecutionUuid(cromwellId);
        }
        String userName = (String) msgJson.get("userName");
        if(StringUtils.isNotEmpty(userName)){
            jobsDTO.setUserName(userName);
        }
        String jobName = (String) msgJson.get("jobName");
        if(StringUtils.isNotEmpty(jobName)){
            jobsDTO.setJobName(jobName);
        }
        return jobsDTO;
    }


}
