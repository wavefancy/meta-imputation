package com.imputation.cromwell.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public class HttpClientUtil {
    /**
     * 上传文件
     * @param filePathMap 上传的文件地址
     * @param url 上传的地址
     * @returnPath
     */
    public static String httpClientUploadFileByPath(Map<String, String> filePathMap,String url ) {
        log.info("上传文件filePathMap="+ JSON.toJSONString(filePathMap));
        log.info("上传地址url="+url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            for (String fileKey :filePathMap.keySet()) {
                String filePath = filePathMap.get(fileKey);
                File file = new File(filePath);
                FileInputStream fileInputStream = new FileInputStream(file);

                MultipartFile multipartFile = new MockMultipartFile("copy"+file.getName(),file.getName(),
                        ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);

                String fileName = multipartFile.getOriginalFilename();
                builder.addBinaryBody(fileKey, multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            log.error("上传文件IOException",e);
            e.printStackTrace();
        } catch (Exception e) {
            log.error("上传文件Exception",e);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("关闭httpClient",e);
                e.printStackTrace();
            }
        }
        log.info("上传文件result="+result);
        return result;
    }
    /**
     * 上传文件
     * @param fileMap 上传的文件
     * @param url 上传的地址
     * @returnPath
     */
    public static String httpClientUploadFileByfile(Map<String, File> fileMap,String url ) {
        log.info("上传地址url="+url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            for (String fileKey :fileMap.keySet()) {
                File file = fileMap.get(fileKey);
                FileInputStream fileInputStream = new FileInputStream(file);
                MultipartFile multipartFile = new MockMultipartFile("copy"+file.getName(),file.getName(),
                        ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);

                String fileName = multipartFile.getOriginalFilename();
                builder.addBinaryBody(fileKey, multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            log.error("上传文件IOException",e);
            e.printStackTrace();
        } catch (Exception e) {
            log.error("上传文件Exception",e);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("关闭httpClient",e);
                e.printStackTrace();
            }
        }
        log.info("上传文件result="+result);
        return result;
    }

    /**
     * httpget无参数请求
     * @param url
     * @return
     */
    public static HashMap<String,Object>  get(String url){

        Boolean flag = true;
        HashMap<String,Object> resMap = new HashMap<>();

        String result = "";
        HttpGet get = new HttpGet(url);
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpResponse response = httpClient.execute(get);

            result = getHttpEntityContent(response);
            if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK){
                log.info("get 请求"+url+"返回response=\n"+response.toString());
                result = "服务器异常";
                flag=false;
            }
            resMap.put("result",result);
            resMap.put("flag",flag);
        } catch (Exception e){
            log.info("url="+url+"请求异常");
            throw new RuntimeException(e);
        } finally{
            get.abort();
        }
        log.info("get请求"+url+"结果resMap="+JSONObject.toJSONString(resMap));
        return resMap;
    }

    /**
     * httpget有参数请求
     * @param paramMap
     * @param url
     * @return
     */
    public static HashMap<String,Object>  get(Map<String, String> paramMap, String url){

        Boolean flag = true;
        HashMap<String,Object> resMap = new HashMap<>();

        String result = "";
        HttpGet get = new HttpGet(url);
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();
            List<NameValuePair> params = setHttpParams(paramMap);
            String param = URLEncodedUtils.format(params, "UTF-8");
            get.setURI(URI.create(url + "?" + param));
            HttpResponse response = httpClient.execute(get);
            result = getHttpEntityContent(response);

            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                result = "服务器异常";
                flag=false;
            }
            resMap.put("result",result);
            resMap.put("flag",flag);
        } catch (Exception e){
            log.info("url="+url+"请求异常");
            throw new RuntimeException(e);
        } finally{
            get.abort();
        }
        log.info("有参get请求结果resMap="+JSONObject.toJSONString(resMap));
        return resMap;
    }

    public static List<NameValuePair> setHttpParams(Map<String, String> paramMap){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Set<Map.Entry<String, String>> set = paramMap.entrySet();
        for(Map.Entry<String, String> entry : set){
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return params;
    }
    /**
     * httppost请求
     * @param json
     * @param url
     * @return
     */
    public static HashMap<String,Object>  post(Object json, String url){
        String jsonStr = null;
        if(json != null && json instanceof JSONObject ){
            jsonStr = ((JSONObject)json).toJSONString();
        }else if(json != null && json instanceof JSONArray ){
            jsonStr = ((JSONArray)json).toJSONString();
        }
        log.info("httppost请求json"+jsonStr);
        log.info("httppost请求url="+url);
        Boolean flag = true;
        HashMap<String,Object> resMap = new HashMap<>();

        HttpPost post = new HttpPost(url);
        BufferedReader br=null;
        InputStream inputStream=null;
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();

            StringEntity postingString = new StringEntity(jsonStr,"utf-8");
            post.setEntity(postingString);
            post.setHeader("Content-Type","application/json;charset=utf-8");

            HttpResponse response = httpClient.execute(post);
            log.info("post结果response="+JSONObject.toJSONString(response));

            inputStream = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }

            String res = strber.toString();
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                res = "服务器异常";
                flag = false;
            }
            resMap.put("result",res);
            resMap.put("flag",flag);
        } catch (Exception e){
            log.info("url="+url+"请求异常");
            throw new RuntimeException(e);
        } finally{
            //关闭流
            try {
                if (inputStream!=null){
                    inputStream.close();
                }
                if (br!=null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            post.abort();
        }
        log.info("post结果resMap="+JSONObject.toJSONString(resMap));
        return resMap;
    }
    public static String getHttpEntityContent(HttpResponse response) throws UnsupportedOperationException, IOException{
        String result = "";
        HttpEntity entity = response.getEntity();
        if(entity != null){
            InputStream in = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            br.close();
            in.close();
            result = strber.toString();
        }

        return result;

    }
}
