package com.imputation.jobs.rabbitmq.service;


import com.imputation.jobs.rabbitmq.dto.MQMessageDTO;

/**
 * @author fanshupeng
 * @create 2023/4/3 16:23
 */
public interface ProducerService {
    /**
     * 发送消息
     * @param messageDTO
     * @return
     */
    boolean sendCromwellMessage(MQMessageDTO messageDTO);
}
