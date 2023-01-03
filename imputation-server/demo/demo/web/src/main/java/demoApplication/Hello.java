package demoApplication;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import my.entity.ResultEnum;
import my.exception.MyException;

@RestController
public class Hello {
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		//返回Hello SpringBoot!
		System.out.println("!~~~~~~~~~~~~~~~~~~~~0");
		return "Hello SpringBoot!";
	}
	
	@RequestMapping(value = "/hello1", method = RequestMethod.GET)
	public void hello1() {
		//返回Hello SpringBoot!
		System.out.println("!~~~~~~~~~~~~~~~~~~~~1");
		throw new MyException(ResultEnum.NO_DATA);
	}
}
