package com.imputation.cromwell.rabbitmq.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fanshupeng
 * @create 2023/4/3 16:39
 */
@Getter
@Setter
public class MQMessageDTO {
    //消息id
    private String msgId;
    //发送方式 t
    private String tag;
    //交换机
    private String exchange;
    //路由key
    private String routingKey;
    //消息
    private String message;
}
