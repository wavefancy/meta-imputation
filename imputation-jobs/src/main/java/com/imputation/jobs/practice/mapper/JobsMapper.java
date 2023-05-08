package com.imputation.jobs.practice.mapper;

import com.imputation.jobs.practice.entity.Jobs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 工作流数据表 Mapper 接口
 * </p>
 *
 * @author fansp
 * @since 2023-05-05
 */
@Mapper
public interface JobsMapper extends BaseMapper<Jobs> {

}
