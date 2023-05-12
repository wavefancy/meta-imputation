package com.imputation.cromwell.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imputation.cromwell.utils.ProcessUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanshupeng
 * @create 2023/5/12 15:45
 */

@Component
@Slf4j
public class MessageConsumerShanghai {

    /*private static final String IMPUTATION_UPLOAD_SHANGHAI_QUEUE_NAME = "imputation.upload.file.queue.shanghai" ;
    @Autowired
    private ProcessUtils processUtils;
    *//**
     * 目标地址
     *//*
    @Value("${rsync.destination.path.shanghai}")
    private  String destinationPathShanghai;
    *//**
     * 监听文件上传消息
     * @param message
     * @param channel
     * @throws IOException
     *//*
    @RabbitListener(queues =IMPUTATION_UPLOAD_SHANGHAI_QUEUE_NAME)
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
            boolean rsyncFlag = processUtils.rsyncPull(sourcePath,destinationFileName,destinationPathShanghai);

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
    }*/

}
