package com.imputation.jobs.utils;

import org.springframework.util.ClassUtils;

import java.io.File;

/**
 * @author fanshupeng
 * @create 2022/8/8 16:17
 * 路径工具类
 * 功能：
 *      1、获取存放服务器文件列表的资源文件路径
 *      2、获取服务器存放文件的目录路径
 */
public class PathUtils {
    /**
     * 获取服务器存放文件的目录路径
     * @return 目录路径（String)
     */
    public static String getFileDir(){
        String path= ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1)+"static/file";
        File dir=new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        return path;
    }
}
