package com.imputation.jobs.incrementer;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 自定义ID生成器
 */
@Slf4j
@Component
public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Long nextId(Object entity) {
        // 填充自己的Id生成器，
        log.info("自定义id生成器开始");
        Long id = IdGenerator.generateId();
        log.info("自定义id生成器结束id="+id);
        return id;
    }
}
