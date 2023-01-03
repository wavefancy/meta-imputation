package my.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import bigfileupload.entity.FileChunkDO;
import bigfileupload.entity.FileChunkDTO;
import bigfileupload.entity.FileChunkParam;
import bigfileupload.entity.FileOwnership;
import bigfileupload.service.FileService;
import my.entity.MyResult;
import my.entity.ResultEnum;
import my.entity.ResultUtil;
import my.exception.MyException;
import my.service.UserService;
import my.util.ServiceInvokeUtil;
import my.util.SomeMethod;

@RestController
public class FileUploadController {
	
	@Value("${wrelativePath}")
	String w_relativePath;	
	
	@Value("${lrelativePath}")
	String l_relativePath;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private UserService userService;
		
	private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
	
	@PostMapping("/file/create")
	public MyResult fileCreate(@Valid FileChunkDO fileChunkDO, BindingResult bindingResult, String OS) throws Exception {
		System.out.println("~~~~~~~~~~~fileCreate");
    	if(bindingResult.hasErrors()) {
    		String errormessage = bindingResult.getFieldError().getDefaultMessage();
    		log.error("Controller call failed: errorCode={}, params={}", -1, JSON.toJSONString(fileChunkDO));
    		return ResultUtil.error("-1", errormessage);
    	}
    	int ind = fileChunkDO.getFileName().lastIndexOf(".");
    	if(-1 == ind) {
    		log.error("Controller call failed: errorCode={}, params={}", ResultEnum.FILENAME_ERROR.getCode(), JSON.toJSONString(fileChunkDO));
    		return ResultUtil.error(ResultEnum.FILENAME_ERROR.getCode(),  ResultEnum.FILENAME_ERROR.getMsg());
    	}
    	
    	
 //   	List<FileChunkDO> list = fileService.listByFileMd5(fileChunkDO.getIdentifier());
    	Long fileId = null;//CollectionUtils.isEmpty(list) ? null : list.get(0).getFileId();
    	FileOwnership fileOwnership = new FileOwnership();
    	fileOwnership.setDesc(fileChunkDO.getDesc());
    	fileOwnership.setFileId(fileId);
    	fileOwnership.setStatus("created");
    	fileOwnership.setUploadTime(df.format(new Date()));
    	fileOwnership.setUserId(userService.queryThisUser().getId());
    	
    	String relativePath = OS == "windows" ? w_relativePath : l_relativePath;
    	fileChunkDO.setStatus(null);
    	fileChunkDO.setUpdateTime(null);
    	fileChunkDO.setUploadTime(fileOwnership.getUploadTime());
    	fileChunkDO.setParentId(null);
    	fileChunkDO.setFileType(null);
    	fileChunkDO.setVersion(null);
    	fileChunkDO.setReferenceCnt(null);
    	fileChunkDO.setRelativePath(relativePath + File.separator 
    			+ fileOwnership.getUserId() + File.separator 
    			+ fileChunkDO.getIdentifier() + fileChunkDO.getFileName().substring(ind, fileChunkDO.getFileName().length()));
    	fileChunkDO.setDesc(null);
    	fileId = fileService.createFile(fileChunkDO, fileOwnership);
		return ResultUtil.success(fileId.toString());
	}

	 /**
     * 上传校验
     * @param param
     * @return
	 * @throws Exception 
     */
    @PostMapping("/file/check")
    public MyResult checkUpload(FileChunkParam param) throws Exception {
    	System.out.println("~~~~~~~~~~~checkUpload");
        Map<String, Object> data = new HashMap<>(10);
/*    	if(param.getFileId() == null || param.getFileId() < 1) {
    		log.error("Controller call failed: errorCode={}, params={}", -1, JSON.toJSONString(param));
    		throw new MyException(ResultEnum.FILEID_ERROR);
    	}*/
 //        List<FileChunkDO> fileL = fileService.selectFile(param.getFileId(), false);
    	
        List<FileChunkDO> fileL = fileService.listByFileMd5(param.getIdentifier());
        if(CollectionUtils.isEmpty(fileL)) {
            data.put("uploaded", false);
            return ResultUtil.success(data);
        }
        
        // 处理分片
        List<FileChunkDTO> chunks = fileL.get(0).getFileChunkDTOs();
        Set<Integer> uploadedChunks = new HashSet<Integer>();
        data.put("uploaded", true);
        int index = 0;
		for (FileChunkDTO fileChunkItem : chunks) {
			uploadedChunks.add(chunks.get(index).getChunkNumber());
            index++;
        }
        data.put("uploadedChunks", uploadedChunks);
        return ResultUtil.success(data);
    }
	
	@PostMapping("/file/upload")
	public MyResult chunkUpload(@Valid FileChunkParam param, BindingResult bindingResult, @RequestParam("chunk") MultipartFile chunk) throws Exception {
		System.out.println("~~~~~~~~~~~uploadchunk");
    	if(bindingResult.hasErrors()) {
    		String errormessage = bindingResult.getFieldError().getDefaultMessage();
    		log.error("Controller call failed: errorCode={}, params={}", -1, JSON.toJSONString(param));
    		return ResultUtil.error("-1", errormessage);
    	}
    	
        if (null == chunk) {
        	log.error("Controller call failed: errorCode={}, params={}", -1, JSON.toJSONString(param));
            throw new MyException(ResultEnum.UPLOAD_FILE_FAILURE);
        }/**/
        param.setFile(chunk);
		fileService.uploadFile(param);
		return ResultUtil.success();
	}
	
	@PostMapping("/file/select")
	public MyResult selectFile(Long fileId, boolean allUsers) throws Exception {
		System.out.println("~~~~~~~~~~~selectFile");
		List<FileChunkDO> fileL = fileService.selectFile(fileId, allUsers);
    	fileL.forEach(
        		(f) -> {
					f.setIdentifier(null);
        			f.setRelativePath(null);
        			f.getFileChunkDTOs().forEach((c) -> {
        				c.setFileIdentifier(null);
        				c.setChunkIdentifier(null);
        			})
        		;}
        );
		return ResultUtil.success(fileL);
	}
	
	@PostMapping("/file/delete")
	public MyResult deleteFile(Long fileId) throws Exception {
		System.out.println("~~~~~~~~~~~selectFile");
		fileService.deleteFile(fileId);;
		return ResultUtil.success();
	}

	@PostMapping("/file/checkorigin")
	public MyResult checkUploadOrigin(String identifier) throws Exception {
//	        log.info("文件MD5:" + param.getIdentifier());

		List<FileChunkDO> list = fileService.listByFileMd5(identifier);
		
		Map<String, Object> data = new HashMap<>(1);

		if (CollectionUtils.isEmpty(list)) {
			data.put("uploaded", false);
			return ResultUtil.success(data);
		}
		FileChunkDO fileChunkDO = list.get(0);
		// 处理上传完成的文件
		if (list.get(0).getTotalChunks() == fileChunkDO.getFileChunkDTOs().size()) {
			data.put("uploaded", true);
			// todo 返回 url
			data.put("url", "");
			return ResultUtil.success(data);
		}
		// 处理分片
		int[] uploadedFiles = new int[fileChunkDO.getFileChunkDTOs().size()];
		int index = 0;
		for (FileChunkDTO fileChunkItem : fileChunkDO.getFileChunkDTOs()) {
			uploadedFiles[index] = fileChunkItem.getChunkNumber();
			index++;
		}
		data.put("uploadedChunks", uploadedFiles);
		return ResultUtil.success(data); 
	}
	
	private String loginSlaveServer(String url) throws Exception {
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
    	paramMap.add("username", "Lee");
    	paramMap.add("password", "Lee");
		
		String res = SomeMethod.restMultipartRequest(url, paramMap, null);
		return res;
	}
	
	private MultiValueMap<String, Object> extractFileInfo(String filePath, String fileName) throws Exception {
		InputStream ins = null;
		MessageDigest md5 = null;
		File file = null;
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
		byte[] buffer = new byte[8192];
		int len;
		
		try {
			file = new File(filePath + File.separator + fileName);  	
			ins = new FileInputStream(file);
			md5 = MessageDigest.getInstance("MD5");
			while((len = ins.read(buffer)) != -1) {
				md5.update(buffer, 0, len);
			}
		} catch(Exception e) {
			log.error("Service call failed: errorCode={}, params={}"
					, ResultEnum.FILE_EXTRACT_INFO_FAILURE.getCode()
					, JSON.toJSONString(filePath + File.separator + fileName));
			throw new MyException(ResultEnum.FILE_EXTRACT_INFO_FAILURE);
		} finally {
			ins.close();
		}
		
    	String identifier = DigestUtils.md5Hex(md5.digest());
    	double sm = (double)file.length() / (double)(20 * 1024 * 1024);
    	Long totalChunks = (long) Math.ceil(sm);
    	paramMap.add("fileSize", file.length());
    	paramMap.add("identifier", identifier);
    	paramMap.add("fileName", fileName);
    	paramMap.add("localPath", filePath);
    	paramMap.add("totalChunks",  totalChunks);
    	paramMap.add("desc", null);
    	paramMap.add("OS", "linux");
    	return paramMap;
	}
	
	private MultiValueMap<String, Object> createFileRecord(String url, String filePath, String fileName, String sessionId) throws Exception {
		MultiValueMap<String, Object> paramMap = extractFileInfo(filePath, fileName);
    	String res = SomeMethod.restMultipartRequest(url, paramMap, sessionId);
    	try {
    		long fileId = Long.parseLong(res);
        	paramMap.add("fileId", fileId);
    		log.debug("Service call sueecss");
    		log.debug("Service call sueecss: errorCode={}, params={}, resutl={}", "0", JSON.toJSONString(res), fileId);
    		return paramMap;
    	} catch(Exception e) {
    		log.error("Service call failed: errorCode={}, params={}", ResultEnum.HTTPRES_FAILURE.getCode(), JSON.toJSONString(res));
        	throw new MyException(ResultEnum.HTTPREQUEST_FAILURE);
    	}
	}
	
	private void uploadFIle(String url, String filePath, String fileName, String sessionId) throws Exception {
		InputStream ins = null;
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
		byte[] buffer = new byte[20 * 1024 *1024];
		int len;
		File file = null;
		
		try {
			file = new File(filePath + File.separator + fileName);  	
			ins = new FileInputStream(file);
			while((len = ins.read(buffer)) != -1) {
				String res = SomeMethod.restMultipartRequest(url, paramMap, sessionId);
			}
		} catch(Exception e) {
			log.error("Service call failed: errorCode={}, params={}"
					, ResultEnum.UPLOAD_FILE_FAILURE.getCode()
					, JSON.toJSONString(filePath + File.separator + fileName));
			throw new MyException(ResultEnum.UPLOAD_FILE_FAILURE);
		} finally {
			ins.close();
		}
	}
	
	private void uploadCheck(String url, String sessionId, String identifier) throws Exception {
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
		
    	paramMap.add("identifier", identifier);
    	
		SomeMethod.restMultipartRequest(url, paramMap, sessionId);
	}
	
	@PostMapping("/file/test")
	public MyResult transferFile(String filePath, String fileName) throws Exception {
		//登录远程服务器
		String sessionId = loginSlaveServer("http://39.103.140.193:9090/cloudimpute/user/login");
		
		//创建文件记录
		MultiValueMap<String, Object> paramMap  = createFileRecord("http://39.103.140.193:9090/cloudimpute/file/create", filePath, fileName, sessionId);
		
		uploadCheck("http://39.103.140.193:9090/cloudimpute/file/check", paramMap.get("identifier").get(0).toString(), sessionId);

		return ResultUtil.success(sessionId); 
	}
	
	
}
