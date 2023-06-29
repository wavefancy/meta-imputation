package com.imputation.jobs.commons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证是否登录注解
 */
@Target({ElementType.METHOD})//可用在方法名上
@Retention(RetentionPolicy.RUNTIME)//运行时生效
public @interface Authorization {
}
