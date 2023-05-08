package com.imputation.jobs.commons;

import java.io.Serializable;
import java.util.Map;

/**
 * 返回结果类
 */
public class BaseResult implements Serializable {

    private static final long serialVersionUID = 1L;
    //成功
    public static final int SUCCESS = 0;
    //失败
    public static final int FAIL = 1;
    //存放数据
    private Object data;
    //结果码
    private int code;
    //消息
    private String msg;

    public BaseResult(){

    }

    public BaseResult(int code){
        this.code = code;
    }

    public BaseResult(int code, Object data){
        this.code = code;
        this.data = data;
    }

    public BaseResult(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public BaseResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data=data;
    }
    /**
     * 成功
     * @param message
     * @return
     */
    public static BaseResult ok(String message){
        return new BaseResult(BaseResult.SUCCESS , message);
    }

    /**
     * 成功
     * @param message
     * @param data
     * @return
     */
    public static BaseResult ok(String message, Object data){
        return new BaseResult(BaseResult.SUCCESS , message, data );
    }

    /**
     * 失败
     * @param message
     * @return
     */
    public static BaseResult error(String message){
        return new BaseResult(BaseResult.FAIL , message);
    }

    /**
     * 自定义数据区域
     * @param code
     * @param msg
     * @param resultMap
     * @return
     */
    public static BaseResult setResultMap(int code, String msg,Map<String,Object> resultMap){
        return new BaseResult(code , msg, resultMap );
    }

    public int getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }

    public Object getData(){
        return this.data;
    }

}
