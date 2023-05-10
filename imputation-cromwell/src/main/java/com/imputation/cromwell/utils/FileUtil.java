package com.imputation.cromwell.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

/**
 * @author fanshupeng
 * @create 2022/4/19 9:55
 */
@Slf4j
public class FileUtil {
    /**
     * 将数据以json格式写入文件
     * @param fileName
     * @param jsonObject
     */
    public static void writerJsonFile(String fileName, JSONObject jsonObject){
        BufferedWriter bw = null;
        try{
            File file = new File(fileName);
            if(!file.exists())//判断文件是否存在，若不存在则新建
            {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream=new FileOutputStream(file);//实例化FileOutputStream
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream,"utf-8");//将字符流转
            //实例化对象
            bw = new BufferedWriter(outputStreamWriter);

            //写入数据
            bw.write(jsonObject.toJSONString());
            bw.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(bw !=  null){
                try{
                    bw.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 将String数据写入文件
     * @param fileName
     * @param str
     */
    public static void writerStringFile(String fileName, String str){
        BufferedWriter bw = null;
        try{
            File file = new File(fileName);
            if(!file.exists())//判断文件是否存在，若不存在则新建
            {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream=new FileOutputStream(file);//实例化FileOutputStream
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream,"utf-8");//将字符流转
            //实例化对象
            bw = new BufferedWriter(outputStreamWriter);

            //写入数据
            bw.write(str);
            bw.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(bw !=  null){
                try{
                    bw.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 读取gz压缩文件数据
     * @param fileName
     * @return
     * @throws IOException
     */
    public static List<String> getGZIPListDataByFileName(String fileName){
        //使用GZIPInputStream解压GZ文件

        if (!getExtension(fileName).equalsIgnoreCase("gz")) {
            log.info("文件名必须以.gz结尾");
        }

        log.info("解压文件开始");
        InputStream in = null;
        try {
            in = new GZIPInputStream(new FileInputStream(fileName));
            Scanner sc=new Scanner(in);
            List<String> lines=new ArrayList();
            while(sc.hasNextLine()){
                lines.add(sc.nextLine());
            }
            return lines;
        } catch(IOException e) {
            log.error("解压gz文件异常",e);
            return null;
        }finally {
            try {
                in.close();
            }catch (IOException e){
                log.error("关闭input流异常",e);
            }
        }
    }
    public static String getGZIPDataHeaderByFileName(String fileName){
        //使用GZIPInputStream解压GZ文件

        if (!getExtension(fileName).equalsIgnoreCase("gz")) {
            log.info("文件名必须以.gz结尾");
        }

        log.info("解压文件开始");
        InputStream in = null;
        try {
            in = new GZIPInputStream(new FileInputStream(fileName));
            Scanner sc=new Scanner(in);
            String header = sc.nextLine();
            log.info("文件头部信息header="+header);
            return header;
        } catch(IOException e) {
            log.error("解压gz文件异常",e);
            return null;
        }finally {
            try {
                in.close();
            }catch (IOException e){
                log.error("关闭input流异常",e);
            }
        }
    }
    public static List<String> getGZIPListDataByMultipartFile(MultipartFile file){
        //使用GZIPInputStream解压GZ文件
        String fileName = file.getName();
        if (!getExtension(fileName).equalsIgnoreCase("gz")) {
            log.info("文件名必须以.gz结尾");
        }

        log.info("解压文件开始");
        InputStream in = null;
        try {
            in = new GZIPInputStream(file.getInputStream());
            Scanner sc=new Scanner(in);
            List<String> lines=new ArrayList();
            while(sc.hasNextLine()){
                lines.add(sc.nextLine());
            }
            return lines;
        } catch(IOException e) {
            log.error("解压gz文件异常",e);
            return null;
        }finally {
            try {
                in.close();
            }catch (IOException e){
                log.error("关闭input流异常",e);
            }
        }
    }
    public static String getGZIPHeaderByMultipartFile(MultipartFile file){
        //使用GZIPInputStream解压GZ文件
        String fileName = file.getName();
        if (!getExtension(fileName).equalsIgnoreCase("gz")) {
            log.info("文件名必须以.gz结尾");
        }

        log.info("解压文件开始");
        InputStream in = null;
        try {
            in = new GZIPInputStream(file.getInputStream());
            Scanner sc=new Scanner(in);
            String header = sc.nextLine();
            log.info("文件头部信息header="+header);
            return header;
        } catch(IOException e) {
            log.error("解压gz文件异常",e);
            return null;
        }finally {
            try {
                in.close();
            }catch (IOException e){
                log.error("关闭input流异常",e);
            }
        }
    }
    public static String getExtension(String f) {
        String ext = "";
        int i = f.lastIndexOf('.');

        if (i > 0 &&  i < f.length() - 1) {
            ext = f.substring(i+1);
        }
        return ext;
    }

    /**
     * 删除文件夹
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
