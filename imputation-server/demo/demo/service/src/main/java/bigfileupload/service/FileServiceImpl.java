package bigfileupload.service;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSON;

import bigfileupload.entity.FileChunkDO;
import bigfileupload.entity.FileChunkDTO;
import bigfileupload.entity.FileChunkParam;
import bigfileupload.entity.FileOwnership;
import my.dao.FileChunkDOMapper;
import my.dao.FileChunkDTOMapper;
import my.dao.FileOwnershipMapper;
import my.entity.ResultEnum;
import my.entity.Users;
import my.exception.MyException;
import my.service.UserService;
import my.util.ServiceInvokeUtil;
import my.util.SomeMethod;

@Service
public class FileServiceImpl implements FileService {
	/**
     * 默认的分片大小：20MB
     */
    public static final long DEFAULT_CHUNK_SIZE = 20 * 1024 * 1024;
    
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    
    @Autowired
    private FileChunkDOMapper fileChunkDOMapper;
    
    @Autowired
    private FileChunkDTOMapper fileChunkDTOMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FileOwnershipMapper fileOwnershipMapper;
    
    @Override
    @Transactional(value = "mysqlTransactionManager")
	public long createFile(FileChunkDO fileChunkDO, FileOwnership fileOwnership) throws Exception {
    	if(null != fileOwnership.getFileId()) {
    		ServiceInvokeUtil.call(() -> fileOwnershipMapper.insert(fileOwnership), ResultEnum.DATABASE_FAILURE, fileOwnership);
    		return fileOwnership.getFileId();
    	}
    	
    	ServiceInvokeUtil.call(() -> fileChunkDOMapper.insert(fileChunkDO), ResultEnum.DATABASE_FAILURE, fileChunkDO);
    	fileOwnership.setFileId(fileChunkDO.getFileId());
    	ServiceInvokeUtil.call(() -> fileOwnershipMapper.insert(fileOwnership), ResultEnum.DATABASE_FAILURE, fileOwnership);
    	
    	return fileOwnership.getFileId();
    }
    
    @Override
    public List<FileChunkDO> listByFileMd5(String md5) throws Exception{       
        List<FileChunkDO> files = ServiceInvokeUtil.call(() -> fileChunkDOMapper.listByMd5(md5), ResultEnum.DATABASE_FAILURE, md5);
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }
        return files;
    }
    
    @Override
	public List<FileChunkDO> selectFile(Long fileId, boolean allUsers) throws Exception {
    	Long userId = null;
    	if(allUsers == false) {
    		Users user = userService.queryThisUser();
    		userId = user.getId();
    	}
    	Map<String, Object> paramMap = new HashMap<String, Object>();       
    	paramMap.put("fileId", fileId); 
    	paramMap.put("userId", userId);
    	List<FileChunkDO> fileL = ServiceInvokeUtil.call(() -> fileChunkDOMapper.selectFile(paramMap), ResultEnum.DATABASE_FAILURE, paramMap);
        return fileL;
    }

    @Override
    @Transactional(value = "mysqlTransactionManager")
    public void uploadFile(FileChunkParam param) throws Exception {
    	
    	FileChunkDO file = ServiceInvokeUtil.call(() -> fileChunkDOMapper.selectByPrimaryKey(param.getFileId()), ResultEnum.DATABASE_FAILURE, param.getFileId());
        
    	//校验参数
    	Long userId = checkFile(param, file);
    	//插入数据块记录
    	boolean flag = commitChunkRecord(param, userId);
    	//上传数据块
    	if(flag)
    		uploadChunk(param, file, userId);
    	else
    		throw new MyException(ResultEnum.CHUNK_EXISTING); 
    }
    
    private Long checkFile(FileChunkParam param, FileChunkDO file) throws Exception {
    	//校验文件块大小
    	long chunkSize = param.getChunkSize() == 0L ? DEFAULT_CHUNK_SIZE : param.getChunkSize();
    	if(chunkSize != param.getFile().getBytes().length) 
    		throw new MyException(ResultEnum.CHUNK_SIZE_ERROR);
    	
    	//校验文件记录是否存在，文件标识符（内容摘要MD5）是否正确
    	if(file == null)
        	throw new MyException(ResultEnum.NOFILE);
        else if(!file.getIdentifier().contentEquals(param.getIdentifier()))
        	throw new MyException(ResultEnum.IDENTIFIER_ERROR);
        
        //用户是否有权访问文件
    	Users user = userService.queryThisUser();
    	Map<String, Object> paramMap = new HashMap<String, Object>();       
    	paramMap.put("fileId", param.getFileId()); 
    	paramMap.put("userId", user.getId());
    	FileOwnership fileOwnership = ServiceInvokeUtil.call(() -> fileOwnershipMapper.checkOwnership(paramMap), ResultEnum.DATABASE_FAILURE, paramMap);
    	
    	if(fileOwnership == null)
    		throw new MyException(ResultEnum.USER_HAS_NOACCESS); 
    	return user.getId();
    }
    
    @Override
    public void createDir(String path) throws Exception {
    	File savePath = new File(path);
        if (!savePath.exists()) {
        	boolean flag = ServiceInvokeUtil.call(
        			() -> savePath.mkdirs(), 
        			ResultEnum.CHUNK_EXISTING, 
        			path);
            if (!flag) {
            	throw new MyException(ResultEnum.PATH_NOTEXISTING);
            }
        }
        return;
    }
    
    private boolean uploadChunk(FileChunkParam param, FileChunkDO file, Long userId) throws Exception {
    	// 判断目录是否存在，不存在则创建目录
    	int ind = file.getRelativePath().indexOf(File.separator);
    	createDir(file.getRelativePath().substring(0, ind));
    	
    	//int fns = param.getFileName().lastIndexOf(".");
        String fullFileName = file.getRelativePath(); //+ param.getFileName().substring(fns);
        
        // 分片上传，这里使用 uploadFileByRandomAccessFile 方法，也可以使用 uploadFileByMappedByteBuffer 方法上传
        param.setFileName(fullFileName);
        
        boolean flag = ServiceInvokeUtil.call(
    			() -> uploadFileByRandomAccessFile(param), 
    			ResultEnum.UPLOAD_FILE_FAILURE, 
    			param);
        return flag;
    }
    
    private boolean commitChunkRecord(FileChunkParam param, Long userId) throws Exception {	
    	FileChunkDTO fileChunkDTO = new FileChunkDTO();
    	fileChunkDTO.setChunkNumber(param.getChunkNumber());
    	fileChunkDTO.setChunkSize(param.getChunkSize());
    	fileChunkDTO.setFileIdentifier(param.getIdentifier());
    	fileChunkDTO.setUserid(userId);
    	fileChunkDTO.setChunkIdentifier(param.getChunk_identifier());
    	fileChunkDTO.setUploadTime(df.format(new Date()));
    	try {		
    		fileChunkDTOMapper.insert(fileChunkDTO);
    	} catch(DuplicateKeyException e) {
    		log.error("Service call failed: errorCode={}, params={}", ResultEnum.CHUNK_EXISTING.getCode(), JSON.toJSONString(fileChunkDTO));
    		return false;
    	} catch(Exception e) {
    		throw new MyException(ResultEnum.DATABASE_FAILURE);
    	}
    	return true;
    }
    
    private boolean uploadFileByRandomAccessFile(FileChunkParam param) {
    	//try(Resource resource = xxx)数据流xxx会在 try执行完毕后自动被关闭，前提是，这些可关闭的资源必须实现 java.lang.AutoCloseable 接口。
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(param.getFileName(), "rw")) {
            
        	// 分片大小必须和前端匹配，否则上传会导致文件损坏
            long chunkSize = param.getChunkSize().longValue();
            
            // 偏移量
            long offset = chunkSize * (param.getChunkNumber() - 1);
           
            // 定位到该分片的偏移量
            randomAccessFile.seek(offset);
           
            // 写入
            randomAccessFile.write(param.getFile().getBytes());
        } catch (Exception e) {
        	log.error("Service call failed: errorCode={}, params={}", ResultEnum.UPLOAD_FILE_FAILURE.getCode(), JSON.toJSONString(param));
    		throw new MyException(ResultEnum.UPLOAD_FILE_FAILURE);
        }
        return true;
    } 
    
    @Override
    @Transactional(value = "mysqlTransactionManager")
    public void deleteFile(Long fileId) throws Exception {
    	if(!hasFileAccess(fileId)) {
    		log.error("Service call failed: errorCode={}, params={}", ResultEnum.NOACCESS.getCode(), JSON.toJSONString(fileId));
    		throw new MyException(ResultEnum.NOACCESS);
    	}
    	
    	FileChunkDO file = ServiceInvokeUtil.call(() -> fileChunkDOMapper.selectByPrimaryKey(fileId), ResultEnum.DATABASE_FAILURE, fileId);
    	if(file == null) {
    		log.error("Service call failed: errorCode={}, params={}", ResultEnum.NOFILE.getCode(), JSON.toJSONString(fileId));
    		throw new MyException(ResultEnum.NOFILE);
    	}
    	
    	ServiceInvokeUtil.call(() -> fileChunkDOMapper.deleteByPrimaryKey(fileId), ResultEnum.DATABASE_FAILURE, fileId);
    	
    	Map<String, Object> paramMap = new HashMap<String, Object>();       
    	paramMap.put("fileId", fileId); 
    	ServiceInvokeUtil.call(() -> fileOwnershipMapper.delete(paramMap), ResultEnum.DATABASE_FAILURE, paramMap);
    
    	List<FileChunkDO> fileL = listByFileMd5(file.getIdentifier());
    	if (CollectionUtils.isEmpty(fileL)) {
    		File saveFile = new File(file.getRelativePath());
    		ServiceInvokeUtil.call(() -> saveFile.delete(), ResultEnum.CANNOT_DELETE, paramMap);
    	}
    		
    }
    
    /**
     * 如果用户拥有这个文件或者具有管理员权限，则具有该文件的访问权限
     * @param fileId
     * @return
     * @throws Exception
     */
    private boolean hasFileAccess(Long fileId) throws Exception {
    	Subject subject = SecurityUtils.getSubject();
    	if(!subject.isAuthenticated())
    		return false;
    	
    	Users user = userService.queryThisUser();
    	Map<String, Object> paramMap = new HashMap<String, Object>();       
    	paramMap.put("fileId", fileId); 
    	paramMap.put("userId", user.getId());
    	FileOwnership fileOwnership = ServiceInvokeUtil.call(() -> fileOwnershipMapper.checkOwnership(paramMap), ResultEnum.DATABASE_FAILURE, paramMap);
    	if(fileOwnership != null)
    		return true;
    	
    	try {
    		subject.checkRole("admin");
    	} catch (Exception e) {
			return false;
		}
    	return true;
    }
    
    private boolean uploadSingleFile(String resultFileName, FileChunkParam param) {
        File saveFile = new File(resultFileName);
        try {
            // 写入
            param.getFile().transferTo(saveFile);
        } catch (Exception e) {
//            log.error("文件上传失败：" + e);
            return false;
        }
        return true;
    }

    public void transferFile() throws Exception {
    	MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
    	SomeMethod.restMultipartRequest("39.103.140.193", map, null);
    }
    
}
