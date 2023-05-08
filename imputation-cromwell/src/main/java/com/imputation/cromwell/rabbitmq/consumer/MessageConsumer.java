package com.imputation.cromwell.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imputation.cromwell.jobs.dto.JobsDTO;
import com.imputation.cromwell.jobs.service.JobsService;
import com.imputation.cromwell.rabbitmq.dto.MQMessageDTO;
import com.imputation.cromwell.rabbitmq.service.ProducerService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author fanshupeng
 * @create 2023/4/10 17:08
 */

@Component
@Slf4j
public class MessageConsumer {
    //imputation topic交换机名称
    private static final String IMPUTATION_TOPIC_EXCHANGE_NAME = "imputation.topic.exchange";

    //imputation running submit队列名称
    public static final String IMPUTATION_RUNNING_SUBMIT_QUEUE_NAME = "imputation.running.submit.queue";

    private static final String IMPUTATION_CROMWELL_SUB_RES_ROUTING_KEY_NAME ="imputation.cromwell.sub.res";

    @Autowired
    private ProducerService producerService;

    @Autowired
    private JobsService jobsService;

    @RabbitListener(queues =IMPUTATION_RUNNING_SUBMIT_QUEUE_NAME)
    public void imputationRunningSubmitMsg(Message message, Channel channel) throws IOException {
        log.info("接受到imputation-job submit队列消息:{}", JSONObject.toJSONString(message));

        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);

        //提交工作流
        JobsDTO jobsDTO = toJobsDTO(msgJson);
        try {
            String cromwellId = jobsService.submitWorkflow(jobsDTO);

            //向前端发送工作流id消息
            Map<String,String> resMsg = new HashMap<>();
            resMsg.put("cromwellId",cromwellId);
            resMsg.put("userName",jobsDTO.getUserName());
            resMsg.put("messageIdOld",messageProperties.getMessageId());
            String messageId = UUID.randomUUID().toString();
            //当前系统时间
            LocalDateTime now = LocalDateTime.now();

            MQMessageDTO messageDTO = new MQMessageDTO();
            messageDTO.setMessage(resMsg.toString());
            messageDTO.setMsgId(messageId);
            messageDTO.setRoutingKey(IMPUTATION_CROMWELL_SUB_RES_ROUTING_KEY_NAME);
            messageDTO.setExchange(IMPUTATION_TOPIC_EXCHANGE_NAME);
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
        return null;
    }


}
