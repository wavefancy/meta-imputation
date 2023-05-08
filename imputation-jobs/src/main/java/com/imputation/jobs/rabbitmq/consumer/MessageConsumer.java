package com.imputation.jobs.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

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


   /* @RabbitListener(queues =IMPUTATION_RUNNING_SUBMIT_QUEUE_NAME)
    public void imputationRunningSubmitMsg(Message message, Channel channel) throws IOException {
        log.info("接受到imputation running submit队列消息:{}", JSONObject.toJSONString(message));

        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);

        try {

        }catch (Exception e){
            log.error("提交cromwell出现异常:{}",e.getMessage());
            // 处理消息异常，将消息重新发送到死信队列中
            channel.basicNack(messageProperties.getDeliveryTag(), false, false);

        }
    }
*/


}
