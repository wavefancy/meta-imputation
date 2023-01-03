package my.entity;

import my.entity.MyResult;

public class ResultUtil {

    public static MyResult success(Object object) {
    	MyResult result = new MyResult();
        result.setCode("0");
        result.setMsg("success");
        result.setData(object);
        return result;
    }

    public static MyResult success() {
        return success(null);
    }

    public static MyResult error(String code, String msg) {
    	MyResult result = new MyResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
