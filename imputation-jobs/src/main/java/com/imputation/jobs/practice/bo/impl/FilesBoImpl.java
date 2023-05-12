package com.imputation.jobs.practice.bo.impl;

import com.imputation.jobs.practice.entity.Files;
import com.imputation.jobs.practice.mapper.FilesMapper;
import com.imputation.jobs.practice.bo.FilesBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文件表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2023-05-11
 */
@Service
public class FilesBoImpl extends ServiceImpl<FilesMapper, Files> implements FilesBo {

}
