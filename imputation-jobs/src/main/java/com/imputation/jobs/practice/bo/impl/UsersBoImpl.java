package com.imputation.jobs.practice.bo.impl;

import com.imputation.jobs.practice.entity.Users;
import com.imputation.jobs.practice.mapper.UsersMapper;
import com.imputation.jobs.practice.bo.UsersBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2023-05-11
 */
@Service
public class UsersBoImpl extends ServiceImpl<UsersMapper, Users> implements UsersBo {

}
