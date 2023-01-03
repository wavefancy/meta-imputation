package my.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import my.entity.MyResult;
import my.entity.ResultUtil;
import my.entity.Users;
import my.service.JobService;
import my.service.UserService;

@RestController
public class JobController {
	@Autowired
	private JobService jobService;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	
	/**处理用户的注册请求
     * @param user
     * @return
     * @throws Exception 
     */
    @PostMapping("/job/create")
    public MyResult userRegister(@Valid Users users, BindingResult bindingResult) throws Exception {
    	System.out.println("~~~~~~~~~~~jobCreate");
    	if(bindingResult.hasErrors()) {
    		String errormessage = bindingResult.getFieldError().getDefaultMessage();
    		return ResultUtil.error("-1", errormessage);
    	}
        String stime = df.format(new Date());

    	return ResultUtil.success(); 
    }
}
