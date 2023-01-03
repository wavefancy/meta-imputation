package my.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import bigfileupload.entity.FileChunkDO;

public interface FileChunkDOMapper {
	
	List<FileChunkDO> listByMd5(@Param("md5") String md5);
	
    int deleteByPrimaryKey(Long fileId);

    int insert(FileChunkDO record);

    int insertSelective(FileChunkDO record);

    FileChunkDO selectByPrimaryKey(Long fileId);

    int updateByPrimaryKeySelective(FileChunkDO record);

    int updateByPrimaryKey(FileChunkDO record);
    
    List<FileChunkDO> selectFile(Map<String, Object> record);
}