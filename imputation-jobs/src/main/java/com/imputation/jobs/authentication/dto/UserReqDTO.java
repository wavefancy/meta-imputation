package com.imputation.jobs.authentication.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 封装登录过滤器解析的用户参数
 */
@Getter
@Setter
public class UserReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键**/
    private String id;

    /**邮箱地址**/
    private String email;

    /**密码**/
    private String password;

    /**名**/
    private String firstName;

    /**姓**/
    private String lastName;

    /**职称**/
    private String jobTitle;

    /**城市**/
    private String city;

    /**国家**/
    private String country;

    /**组织**/
    private String organisation;

    /**性别**/
    private String sex;

    /**年龄**/
    private String age;

    /**证件号**/
    private String idNo;

    /**手机号**/
    private String mobile;
}
