package com.imputation.jobs.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author fanshupeng
 * @create 2022/3/24 13:50
 */
@Slf4j
public class MultipartFileToFileUtil {
    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

        } catch (Exception e) {
            log.error("获取流文件异常",e);
        }finally {
            try {
                if(os != null){
                    os.close();
                    log.info("已关闭os流");
                }
                if(ins != null){
                    ins.close();
                    log.info("已关闭ins流");
                }
            }catch (Exception e){
                log.error("流关闭异常",e);
            }
        }
    }

    /**
     * 删除本地临时文件
     *
     * @param file
     */
    public static void delteTempFile(File file) {
        if(file != null && file.exists()) {//判断路径是否存在
            if(file.isFile()){//boolean isFile():测试此抽象路径名表示的文件是否是一个标准文件。
                file.delete();
            }else{//不是文件，对于文件夹的操作
                //保存 路径下的所有的文件和文件夹到listFiles数组中
                File[] listFiles = file.listFiles();//listFiles方法：返回file路径下所有文件和文件夹的绝对路径
                for (File file2 : listFiles) {
                    /*
                     * 递归作用：由外到内先一层一层删除里面的文件 再从最内层 反过来删除文件夹
                     *    注意：此时的文件夹在上一步的操作之后，里面的文件内容已全部删除
                     *         所以每一层的文件夹都是空的  ==》最后就可以直接删除了
                     */
                    delteTempFile(file2);
                }
            }
            //删除空文件夹
            file.delete();
        }else {
            log.info("该file路径不存在！！");
        }
    }
}

