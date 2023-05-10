package com.imputation.cromwell.rabbitmq.config;

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
    //imputation Direct交换机名称
    private static final String IMPUTATION_DIRECT_EXCHANGE_NAME = "imputation.direct.exchange";
    //imputation死信交换机名称
    private static final String IMPUTATION_EXCHANGE_DLX_NAME = "imputation.exchange.dlx";

    //imputation running submit队列名称
    public static final String IMPUTATION_RUNNING_SUBMIT_QUEUE_NAME = "imputation.running.submit.queue";
    //imputation死信队列名称
    public static final String DEAD_LETTER_QUEUE_QUEUE_NAME = "imputation.dead.letter.queue";

    // 查询未结束的工作流状态消息
    private static final String QUERY_JOBS_STATUS_QUEUE_NAME = "query.jobs.status.queue";
    private static final String QUERY_JOBS_STATUS_ROUTING_KEY_NAME = "query.jobs.status.routing.key";


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
        return BindingBuilder.bind(queue).to(exchange).with("imputation.job.submit");
    }
    // 声明QUERY_JOBS_STATUS_QUEUE_NAME队列
    @Bean("queryJobsStatusQueue")
    public Queue queryJobsStatusQueue(){
        return QueueBuilder.durable(QUERY_JOBS_STATUS_QUEUE_NAME).build();
    }
    // 声明queryJobsStatusQueue队列绑定关系
    @Bean("queryJobsStatusQueueBinding")
    public Binding queryJobsStatusQueueBinding(@Qualifier("queryJobsStatusQueue") Queue queue,
                                @Qualifier("imputationDirectExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(QUERY_JOBS_STATUS_ROUTING_KEY_NAME);
    }
}
