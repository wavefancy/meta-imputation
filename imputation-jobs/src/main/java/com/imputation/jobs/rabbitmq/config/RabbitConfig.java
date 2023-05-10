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



    //imputation running submit 结果返回队列名称
    public static final String IMPUTATION_RUNNING_SUBMIT_RES_QUEUE_NAME = "imputation.running.submit.res.queue";
    private static final String IMPUTATION_RUNNING_SUBMIT_RES_ROUTING_KEY_NAME ="imputation.running.submit.res";

    // 查询未结束的工作流状态结果消息队列与路由
    private static final String QUERY_JOBS_STATUS_RES_QUEUE_NAME ="query.jobs.status.res.queue";
    private static final String QUERY_JOBS_STATUS_RES_ROUTING_KEY_NAME ="query.jobs.status.res";


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
    // imputation running submit 结果返回队列
    @Bean("imputationRunningSubmitResQueue")
    public Queue imputationRunningSubmitResQueue(){
        return QueueBuilder.durable(IMPUTATION_RUNNING_SUBMIT_RES_QUEUE_NAME).build();
    }
    // 声明imputationRunningSubmitResQueue队列绑定关系
    @Bean("imputationRunningSubmitResQueueBinding")
    public Binding imputationRunningSubmitResQueueBinding(@Qualifier("imputationRunningSubmitResQueue") Queue queue,
                                @Qualifier("imputationDirectExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(IMPUTATION_RUNNING_SUBMIT_RES_ROUTING_KEY_NAME);
    }
    // 查询未结束的工作流状态结果消息队列
    @Bean("queryJobsStatusResQueue")
    public Queue queryJobsStatusResQueue(){
        return QueueBuilder.durable(QUERY_JOBS_STATUS_RES_QUEUE_NAME).build();
    }
    // 声明查询未结束的工作流状态结果消息队列绑定关系
    @Bean("queryJobsStatusResQueueBinding")
    public Binding queryJobsStatusResQueueBinding(@Qualifier("queryJobsStatusResQueue") Queue queue,
                                @Qualifier("imputationDirectExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(QUERY_JOBS_STATUS_RES_ROUTING_KEY_NAME);
    }
}
