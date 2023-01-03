package my.dao;

import java.util.Map;

import bigfileupload.entity.FileOwnership;

public interface FileOwnershipMapper {
    int deleteByPrimaryKey(Long ownershipId);

    int insert(FileOwnership record);

    int insertSelective(FileOwnership record);

    FileOwnership selectByPrimaryKey(Long ownershipId);

    int updateByPrimaryKeySelective(FileOwnership record);

    int updateByPrimaryKey(FileOwnership record);
    
    FileOwnership checkOwnership(Map<String, Object> record);
    
    int delete(Map<String, Object> record);
}