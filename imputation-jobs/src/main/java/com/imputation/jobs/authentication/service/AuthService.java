package com.imputation.jobs.authentication.service;


import com.imputation.jobs.commons.BaseResult;
import com.imputation.jobs.practice.entity.Users;

public interface AuthService {
    /**
     * 新增或修改user
     * @param users
     * @return
     */
    BaseResult saveOrUpdateUser(Users users);

    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    BaseResult getUserInfoById(Long id);

    /**
     * 根据传入信息获取用户信息
     * @param users
     * @return
     */
    BaseResult getUserInfo(Users users);

    /**
     * 删除用户
     * @param users
     * @return
     */
    BaseResult deleteUser(Users users);
    /**
     * 统计用户个数
     * @return
     */
    BaseResult userCount();
}
