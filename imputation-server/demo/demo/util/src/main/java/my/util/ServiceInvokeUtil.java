package my.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import my.entity.ResultEnum;
import my.exception.MyException;

import java.util.function.Supplier;

public class ServiceInvokeUtil {
   private static final Logger log = LoggerFactory.getLogger(ServiceInvokeUtil.class);

   public static <T> T call(Supplier<T> supplier, ResultEnum resultEnum, Object... params) {
       try {
    	   log.debug("Service call sueecss");
    	   log.debug("Service call sueecss: errorCode={}, params={}, resutl={}", "0", JSON.toJSONString(params), "3");
           return supplier.get();
       } catch (Throwable var) {
    	   log.error("Service call failed: errorCode={}, params={}", resultEnum.getCode(), JSON.toJSONString(params));
           throw new MyException(resultEnum);
       }
   }

   public static <T> void callWithoutResult(Supplier<T> supplier, ResultEnum resultEnum, Object... params) {
       try {
    	   log.debug("Service call sueecss");
    	   log.debug("Service call sueecss: errorCode={}, params={}, resutl={}", "0", JSON.toJSONString(params), "3");
           return;
       } catch (Throwable var) {
    	   log.error("Service call failed: errorCode={}, params={}", resultEnum.getCode(), JSON.toJSONString(params));
           throw new MyException(resultEnum);
       }
   }
   
   public static void checkResult(boolean condition, ResultEnum resultEnum, Object... params) {
       if (!condition) {
           throw new MyException(resultEnum);
       }
   }

    private static String getCode(ResultEnum resultEnum) {
       return null == resultEnum ?
               "null" : String.format("%s_%s",resultEnum.getCode(), resultEnum.getMsg());
    }
}

