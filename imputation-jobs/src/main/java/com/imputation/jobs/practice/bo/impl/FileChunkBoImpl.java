package com.imputation.jobs.practice.bo.impl;

import com.imputation.jobs.practice.entity.FileChunk;
import com.imputation.jobs.practice.mapper.FileChunkMapper;
import com.imputation.jobs.practice.bo.FileChunkBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 分片表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2023-05-11
 */
@Service
public class FileChunkBoImpl extends ServiceImpl<FileChunkMapper, FileChunk> implements FileChunkBo {

}
