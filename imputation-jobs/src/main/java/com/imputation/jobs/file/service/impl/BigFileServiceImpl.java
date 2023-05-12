package com.imputation.jobs.file.service.impl;

import com.imputation.jobs.file.dto.ChunkDTO;
import com.imputation.jobs.file.dto.FileChunkReqDTO;
import com.imputation.jobs.file.service.BigFileService;
import com.imputation.jobs.file.service.FileChunkService;
import com.imputation.jobs.utils.PathUtils;
import com.imputation.jobs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanshupeng
 * @create 2022/8/8 15:59
 */
@Slf4j
@Service
public class BigFileServiceImpl implements BigFileService {
    /**
     * 默认的分片大小：10MB
     */
    public static final long DEFAULT_CHUNK_SIZE = 10 * 1024 * 1024;

    @Value("${upload.file.path}")
    private String uploadFilePath  ;
    @Autowired
    private FileChunkService fileChunkService;

    @Override
    public String fileUploadPost(ChunkDTO chunk, HttpServletResponse response) {
        /**
         * 每一个上传块都会包含如下分块信息：
         * chunkNumber: 当前块的次序，第一个块是 1，注意不是从 0 开始的。
         * totalChunks: 文件被分成块的总数。
         * chunkSize: 分块大小，根据 totalSize 和这个值你就可以计算出总共的块数。注意最后一块的大小可能会比这个要大。
         * currentChunkSize: 当前块的大小，实际大小。
         * totalSize: 文件总大小。
         * identifier: 这个就是每个文件的唯一标示。
         * filename: 文件名。
         * relativePath: 文件夹上传的时候文件的相对路径属性。
         * 一个分块可以被上传多次，当然这肯定不是标准行为，但是在实际上传过程中是可能发生这种事情的，这种重传也是本库的特性之一。
         *
         * 根据响应码认为成功或失败的：
         * 200 文件上传完成
         * 201 文加快上传成功
         * 500 第一块上传失败，取消整个文件上传
         * 507 服务器出错自动重试该文件块上传
         */
        String path = PathUtils.getFileDir();
        String fileName = chunk.getFilename();
        log.info("文件存放路径="+path+fileName);
        File file= new File(PathUtils.getFileDir(), chunk.getFilename());
        //第一个块,则新建文件
        if(chunk.getChunkNumber()==1 && !file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                response.setStatus(500);
                return "exception:createFileException";
            }
        }

        //进行写文件操作
        try(
                //将块文件写入文件中
                InputStream fos=chunk.getFile().getInputStream();
                RandomAccessFile raf =new RandomAccessFile(file,"rw")
        ) {
//            int len=-1;
//            byte[] buffer=new byte[1024];
//            raf.seek((chunk.getChunkNumber()-1)*1024*1024*5);
//            while((len=fos.read(buffer))!=-1){
//                raf.write(buffer,0,len);
//            }
            int len=-1;
            byte[] buffer=new byte[1024];
            raf.seek((chunk.getChunkNumber()-1)*1024*1024);
            while((len=fos.read(buffer))!=-1){
                raf.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(chunk.getChunkNumber()==1) {
                file.delete();
            }
            response.setStatus(507);
            return "exception:writeFileException";
        }
        if(chunk.getChunkNumber().equals(chunk.getTotalChunks())){
            response.setStatus(200);
            // TODO 向数据库中保存上传信息
            return "over";
        }else {
            response.setStatus(201);
            return "ok";
        }
    }

    @Override
    public Map<String,Object> uploadFile(String email, FileChunkReqDTO param) {
        HashMap<String,Object> resMap = new HashMap<>();
        MultipartFile paramFile = param.getFile();
        Boolean resFlag = false;
        if (null == paramFile) {
            log.info("上传文件不能为空");
            resMap.put("flag",resFlag);
            return resMap;
        }

        String fileNameInput = param.getFileNameInput();
        String fileName = param.getFilename();

        String onlyName = StringUtils.isNotEmpty(fileNameInput) ? fileNameInput : fileName.substring(0 ,fileName.indexOf("."));
        // 判断目录是否存在，不存在则创建目录
        File savePath = new File(uploadFilePath+ File.separator + email + File.separator + param.getIdentifier()+ File.separator+onlyName );
        if (!savePath.exists()) {
            boolean flag = savePath.mkdirs();
            if (!flag) {
                log.error("保存目录创建失败");
                resMap.put("flag",resFlag);
                return resMap;
            }
        }

        //获取到后缀名
        String suffixName = fileName.contains(".") ? fileName.substring(fileName.indexOf(".")) : null;
        log.info("后缀名suffixName="+suffixName);

        // 这里可以使用 uuid 来指定文件名，上传完成后再重命名
        String fullFileName = savePath + File.separator + (StringUtils.isNotEmpty(fileNameInput) ? fileNameInput+suffixName : fileName);
        log.info("文件完整路径fullFileName="+fullFileName);

        resMap.put("fileName",onlyName);
        resMap.put("suffixName",suffixName);
        resMap.put("filePath",savePath + File.separator );

        // 单文件上传
        if (param.getTotalChunks() == 1) {
            resFlag =  uploadSingleFile(fullFileName, param);
            resMap.put("flag",resFlag);
            return resMap;
        }
        // 分片上传，这里使用 uploadFileByRandomAccessFile 方法，也可以使用 uploadFileByMappedByteBuffer 方法上传
        resFlag = uploadFileByRandomAccessFile(fullFileName, param);

        if (resFlag) {
            // 保存分片上传信息
            //修改为页面输入的文件名
            param.setFilename(fileNameInput+suffixName);
            fileChunkService.saveFileChunk(param);
        }
        resMap.put("flag",resFlag);
        return resMap;
    }

    private boolean uploadFileByRandomAccessFile(String resultFileName, FileChunkReqDTO param) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(resultFileName, "rw")) {
            // 分片大小必须和前端匹配，否则上传会导致文件损坏
            long chunkSize = param.getChunkSize() == 0L ? DEFAULT_CHUNK_SIZE : param.getChunkSize().longValue();
            // 偏移量
            long offset = chunkSize * (param.getChunkNumber() - 1);
            // 定位到该分片的偏移量
            randomAccessFile.seek(offset);
            // 写入
            randomAccessFile.write(param.getFile().getBytes());

        } catch (IOException e) {
            log.error("文件上传失败：" + e);
            return false;
        }
        return true;
    }
    private boolean uploadSingleFile(String resultFileName, FileChunkReqDTO param) {
        File saveFile = new File(resultFileName);
        try {
            // 写入
            param.getFile().transferTo(saveFile);
        } catch (IOException e) {
            log.error("文件上传失败：" + e);
            return false;
        }
        return true;
    }
}
