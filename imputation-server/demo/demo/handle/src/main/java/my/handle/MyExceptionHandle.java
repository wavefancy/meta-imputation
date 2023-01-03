package my.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import my.entity.MyResult;
import my.entity.ResultUtil;
import my.exception.MyException;


@ControllerAdvice
public class MyExceptionHandle {
	
//	private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public MyResult handle(Exception e) {
		if (e instanceof MyException) {
			MyException myException = (MyException) e;
			return ResultUtil.error(myException.getCode(), myException.getMessage());
		}
		else {
//        	logger.error("【系统异常】{}", e);
			return ResultUtil.error("-1", "未知错误");
        }
    }
}
