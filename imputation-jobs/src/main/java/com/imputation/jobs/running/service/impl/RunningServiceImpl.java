package com.imputation.jobs.running.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.imputation.jobs.practice.bo.JobsBo;
import com.imputation.jobs.practice.entity.Jobs;
import com.imputation.jobs.running.dto.JobsDTO;
import com.imputation.jobs.running.service.RunningService;
import com.imputation.jobs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanshupeng
 * @create 2023/4/26 16:52
 */
@Service
@Slf4j
public class RunningServiceImpl implements RunningService {
    @Autowired
    private JobsBo jobsBo;

    /**
     * 保存或修改job数据
     * @param jobsDTO
     * @return
     */
    @Override
    public boolean saveOrUpdateJob(JobsDTO jobsDTO) {
        log.info("保存或修改job数据入参：{}", JSONObject.toJSONString(jobsDTO));
        Jobs jobs = new Jobs();
        BeanUtils.copyProperties(jobsDTO,jobs);
        UpdateWrapper<Jobs> updateWrapper = new UpdateWrapper();
        if(jobsDTO.getId() != null){
            updateWrapper.eq("id",jobsDTO.getId());
        }
        if(StringUtils.isNotEmpty(jobsDTO.getJobName())){
            updateWrapper.eq("job_name",jobsDTO.getJobName());
        }
        if(StringUtils.isNotEmpty(jobsDTO.getUserName())){
            updateWrapper.eq("user_name",jobsDTO.getUserName());
        }
        boolean flag = jobsBo.saveOrUpdate(jobs,updateWrapper);
        log.info("保存或修改job数据出参：{}", flag);
        return flag;
    }
    /**
     * 查询Jobs
     * @param jobsDTO
     * @return
     */
    @Override
    public List<JobsDTO> queryJobs(JobsDTO jobsDTO) {
        log.info("查询Jobs入参：{}",JSONObject.toJSONString(jobsDTO));

        QueryWrapper<Jobs> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(jobsDTO.getJobName())){
            queryWrapper.eq("job_name",jobsDTO.getJobName());
        }
        if(StringUtils.isNotEmpty(jobsDTO.getUserName())){
            queryWrapper.eq("user_name",jobsDTO.getUserName());
        }
        queryWrapper.eq("is_delete",0);
        List<Jobs> jobsList = jobsBo.list(queryWrapper);

        if(CollectionUtils.isEmpty(jobsList)){
            log.info("查询Jobs出参：null");
            return null;
        }

        List<JobsDTO> jobsDTOList = new ArrayList<JobsDTO>();

        for (Jobs jobs:jobsList) {
            JobsDTO jobsDTORes = new JobsDTO();
            BeanUtils.copyProperties(jobs,jobsDTORes);
            jobsDTOList.add(jobsDTORes);
        }

        log.info("查询Jobs出参：{}",JSONObject.toJSONString(jobsDTOList));
        return jobsDTOList;
    }

}
