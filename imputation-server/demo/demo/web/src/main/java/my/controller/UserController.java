package my.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import my.entity.MyResult;
import my.entity.ResultUtil;
import my.entity.Users;
import my.service.UserService;
import my.util.ServiceInvokeUtil;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	private static final Logger log = LoggerFactory.getLogger(ServiceInvokeUtil.class);
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	
	/**处理用户的注册请求
     * @param user
     * @return
     * @throws Exception 
     */
    @PostMapping("/user/register")
    public MyResult userRegister(@Valid Users users, BindingResult bindingResult) throws Exception {
    	System.out.println("~~~~~~~~~~~userRegister");
    	if(bindingResult.hasErrors()) {
    		String errormessage = bindingResult.getFieldError().getDefaultMessage();
    		log.error("Controller call failed: errorCode={}, params={}", -1, JSON.toJSONString(users));
    		return ResultUtil.error("-1", errormessage);
    	}
        String stime = df.format(new Date());
        users.setUserRegTime(stime);
        if(users.getPassword() == null || "".equals(users.getPassword())) {
        	users.setPassword("123456");
        }
        userService.userRegister(users, "user");
    	return ResultUtil.success(); 
    }
    
    /**处理用户的注册请求
     * @param user
     * @return
     * @throws Exception 
     */
    @PostMapping("/user/unregister")
    public MyResult userUnRegister(@Valid Users users, BindingResult bindingResult) throws Exception {
    	System.out.println("~~~~~~~~~~~userUnRegister");
    	if(bindingResult.hasErrors()) {
    		String errormessage = bindingResult.getFieldError().getDefaultMessage();
    		return ResultUtil.error("-1", errormessage);
    	}
        String stime = df.format(new Date());
        users.setUserUnregTime(stime);
        users.setUserStatus("unRegistered");
        if(users.getPassword() == null || users.getPassword() == "") {
        	users.setPassword("123456");
        }
        userService.userUpdate(users);
    	return ResultUtil.success(); 
    }
    
    /**处理用户信息更新请求
     * @param user
     * @return
     * @throws Exception 
     */
    @PostMapping("/user/update")
    public MyResult userUpdate(Users users) throws Exception {
		System.out.println("~~~~~~~~~~~userUpdate");
        long ti = userService.userUpdate(users);
        return ResultUtil.success();
    }
    
    /**处理用户的登录请求
     */
    @PostMapping("/user/login")
    public MyResult userLogin(Users users) throws Exception {
    	System.out.println("~~~~~~~~~~~userLogin");
    	if(users.getUsername() == null || users.getPassword() == null) {
     		return ResultUtil.error("-1", "请填写正确的用户名和密码");
     	}
     	userService.userLogin(users);
        return ResultUtil.success(SecurityUtils.getSubject().getSession().getId());
    }
    
    @PostMapping("/user/logout")
    public MyResult userLogout() {
    	System.out.println("~~~~~~~~~~~userLogout");
    	userService.userLogout();
    	return ResultUtil.success(null); 
    }
    
    @PostMapping("/user/querythisuser")
    public MyResult queryThisVendor() throws Exception {
    	System.out.println("~~~~~~~~~~~QueryThisVendor");
    	userService.requireRoles("user");
    	Users us = userService.queryThisUser();
        return ResultUtil.success(us); 
    }
}
