package my.dao;

import bigfileupload.entity.FileChunkDTO;
import bigfileupload.entity.FileChunkDTOKey;

public interface FileChunkDTOMapper {
    int deleteByPrimaryKey(FileChunkDTOKey key);

    int insert(FileChunkDTO record);

    int insertSelective(FileChunkDTO record);

    FileChunkDTO selectByPrimaryKey(FileChunkDTOKey key);

    int updateByPrimaryKeySelective(FileChunkDTO record);

    int updateByPrimaryKey(FileChunkDTO record);
}