package com.imputation.jobs.commons;

import com.imputation.jobs.practice.entity.Users;
import com.imputation.jobs.utils.StringUtils;
import com.imputation.jobs.utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class BaseController {
    /**
     * 校验请必填参数是否为空
     * @param obj
     * @param parameterMap
     * @return
     */
    public boolean reqIsEmpty(Object obj , Map<String,String> parameterMap){
        boolean flag = false;
        for (String  parameter:parameterMap.keySet()) {
            if(StringUtils.isEmpty(parameterMap.get(parameter))){
                flag = true;//必填参数为空
                break;
            }
        }
        return flag;
    }

    /**
     * 生成token
     * @param user
     * @return
     */
    public String getAccessToken(Users user){
        Map<String,String> reqMap = new HashMap<>();
        reqMap.put("email",user.getEmail());
        reqMap.put("password",user.getPassword());
        String token = TokenUtil.token(reqMap);
        return token;
    }

}
