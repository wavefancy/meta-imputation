package com.imputation.jobs.practice.mapper;

import com.imputation.jobs.practice.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author fansp
 * @since 2023-05-11
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}
