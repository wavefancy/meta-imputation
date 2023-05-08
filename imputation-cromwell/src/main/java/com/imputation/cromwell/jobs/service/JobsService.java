package com.imputation.cromwell.jobs.service;

import com.imputation.cromwell.jobs.dto.JobsDTO;

/**
 * @author fanshupeng
 * @create 2023/5/6 17:08
 */
public interface JobsService {
    /**
     * 提交工作流
     * @param jobsDTO
     * @return
     */
    String submitWorkflow(JobsDTO jobsDTO) throws Exception;
}
