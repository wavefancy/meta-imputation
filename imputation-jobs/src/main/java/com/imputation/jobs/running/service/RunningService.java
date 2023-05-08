package com.imputation.jobs.running.service;

import com.imputation.jobs.running.dto.JobsDTO;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2023/4/26 16:52
 */
public interface RunningService {
    /**
     * 保存或修改job数据
     * @param jobsDTO
     * @return
     */
    boolean saveOrUpdateJob(JobsDTO jobsDTO);

    /**
     * 查询Jobs
     * @param jobsDTO
     * @return
     */
    List<JobsDTO> queryJobs(JobsDTO jobsDTO);
}
