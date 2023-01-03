package bigfileupload.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.util.CollectionUtils;

import bigfileupload.entity.FileChunkDO;
import bigfileupload.entity.FileChunkParam;
import bigfileupload.entity.FileOwnership;

public interface FileService {
	
	/**
     * 创建文件记录
     * @param param 参数
     * @return
     */
	public long createFile(FileChunkDO fileChunkDO, FileOwnership fileOwnership) throws Exception;
	
	/**
	 * 按文件摘要查询文件是否曾经上传
	 * @param md5
	 * @return
	 * @throws Exception
	 */
    public List<FileChunkDO> listByFileMd5(String md5) throws Exception;
	
	/**
     * 检索文件
     * @param param 参数
     * @return
     */
	public List<FileChunkDO> selectFile(Long fileId, boolean allUsers) throws Exception;
    
	 /**
     * 上传文件
     * @param param 参数
     * @return
     */
	public void uploadFile(FileChunkParam param) throws Exception;
	
	/**
     * 删除文件
     * @param param 参数
     * @return
     */
	public void deleteFile(Long fileId) throws Exception;
	
	
	/**
	 * 创建目录
	 * @param path
	 * @throws Exception
	 */
	public void createDir(String path) throws Exception;
	
	/**
	 * 将文件发送到远端服务器
	 * @throws Exception
	 */
	public void transferFile() throws Exception;
    
}
