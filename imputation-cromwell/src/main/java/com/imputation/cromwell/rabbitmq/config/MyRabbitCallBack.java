package com.imputation.cromwell.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author fanshupeng
 * @create 2023/3/24 10:11
 * 发布确认回调函数
 */
@Component
@Slf4j
public class MyRabbitCallBack implements RabbitTemplate.ConfirmCallback ,RabbitTemplate.ReturnsCallback{
    @Autowired
    private RabbitTemplate rabbitTemplate ;
    //依赖注入 rabbitTemplate 之后再设置它的回调对象
    @PostConstruct
    public void init ( ) {
        //注入确认回调
        rabbitTemplate.setConfirmCallback(this);
        /**
         * true：
         * 交换机无法将消息进行路由时，会将该消息返回给生产者
         * false：
         * 消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        //设置回退消息交给谁处理
        rabbitTemplate.setReturnsCallback(this);
    }
    /**
     *
     * @param correlationData 消息相关数据
     * @param ack 交换机是否收到消息
     * @param cause 未收到消息原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId():"";
        if(ack){
            log.info("交换机已经收到 id 为:{}的消息",id);
        }else{
            log.info("交换机还未收到 id 为:{}消息,由于原因:{}",id,cause);
        }

    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.info("消息:{}被服务器退回，退回原因:{}, 交换机是:{}, 路由 key:{}",
                new String(returned.getMessage().getBody()),returned.getReplyText(), returned.getExchange(), returned.getRoutingKey());
    }
}
