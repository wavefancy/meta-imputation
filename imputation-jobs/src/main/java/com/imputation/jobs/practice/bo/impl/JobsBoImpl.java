package com.imputation.jobs.practice.bo.impl;

import com.imputation.jobs.practice.entity.Jobs;
import com.imputation.jobs.practice.mapper.JobsMapper;
import com.imputation.jobs.practice.bo.JobsBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工作流数据表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2023-05-05
 */
@Service
public class JobsBoImpl extends ServiceImpl<JobsMapper, Jobs> implements JobsBo {

}
