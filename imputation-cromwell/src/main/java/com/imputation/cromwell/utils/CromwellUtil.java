package com.imputation.cromwell.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanshupeng
 * @create 2022/5/25 9:14
 */
@Slf4j
public class CromwellUtil {

    /**
     * 中止工作流
     * @param uuid
     * @return
     */
    public static Boolean workflowsAbort(String workflowsAbortUrl,String uuid){
        log.info("中止工作流uuid="+uuid);
        Boolean flag = false;
        String url = workflowsAbortUrl.replace("uuid",uuid);
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("id",uuid);
            HashMap<String, Object> res = HttpClientUtil.post(jsonData,url);
            flag = (Boolean) res.get("flag");
        }catch (Exception e){
            log.error("中止工作流uuid="+uuid+"失败",e);
            return flag;
        }
        log.info("中止工作流uuid="+uuid+",\n结果flag="+flag);
        return flag;
    }

    /**
     * 查询工作流状态
     * @param uuid
     * @return
     */
    public static String workflowsStatus(String workflowsStatusUrl,String uuid){
        log.info("查询工作流状态uuid="+uuid);
        Boolean flag = false;
        String status = null;
        String url = workflowsStatusUrl.replace("uuid",uuid);
        try {
            HashMap<String, Object> res = HttpClientUtil.get(url);
            flag = (Boolean) res.get("flag");
            if(flag){
                JSONObject resultJson = (JSONObject) JSON.parse((String) res.get("result"));
                status = (String) resultJson.get("status");
            }
        }catch (Exception e){
            log.error("查询工作流状态uuid="+uuid+"失败",e);
            return status;
        }
        log.info("查询工作流状态uuid="+uuid+",\n结果flag={}，status={}",flag,status);
        return status;
    }

    /**
     * post查询工作流信息
     * @param json
     * @return
     */
    public static Map<String, Object> workflowsQueryPost(String workflowsQueryPostUrl, Object json){
        log.info("post查询工作流信息");
        HashMap<String, Object> res = null;
        try {
            res = HttpClientUtil.post(json,workflowsQueryPostUrl);
        }catch (Exception e){
            log.error("post查询工作流信息失败",e);
            return res;
        }
        log.info("post查询工作流信息结果res="+res.toString());
        return res;
    }

}
