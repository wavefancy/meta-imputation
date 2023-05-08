package com.imputation.jobs.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置文件
 * @author fanshupeng
 * @create 2023/4/7 16:58
 */


@Configuration
public class RabbitConfig {
    //imputation topic交换机名称
    private static final String IMPUTATION_DIRECT_EXCHANGE_NAME = "imputation.direct.exchange";
    //imputation死信交换机名称
    private static final String IMPUTATION_EXCHANGE_DLX_NAME = "imputation.exchange.dlx";

    //imputation running submit队列名称
    public static final String IMPUTATION_RUNNING_SUBMIT_QUEUE_NAME = "imputation.running.submit.queue";
    //imputation死信队列名称
    public static final String DEAD_LETTER_QUEUE_QUEUE_NAME = "imputation.dead.letter.queue";

    //声明imputation direct Exchange
    @Bean("imputationDirectExchange")
    public DirectExchange imputationDirectExchange(){
        return (DirectExchange) ExchangeBuilder.directExchange(IMPUTATION_DIRECT_EXCHANGE_NAME)
                .durable(true)
                .build();
    }
    // 声明ALGORITHMS_PARAMETER队列
    @Bean("imputationRunningSubmitQueue")
    public Queue imputationRunningSubmitQueue(){
        return QueueBuilder.durable(IMPUTATION_RUNNING_SUBMIT_QUEUE_NAME).build();
    }
    // 声明imputationRunningSubmitQueue队列绑定关系
    @Bean("imputationRunningSubmitQueueBinding")
    public Binding imputationRunningSubmitQueueBinding(@Qualifier("imputationRunningSubmitQueue") Queue queue,
                                @Qualifier("imputationDirectExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("imputation.job.submit.result");
    }
}
