package com.imputation.jobs.email.controller;

import com.imputation.jobs.commons.BaseController;
import com.imputation.jobs.email.service.IMailService;
import com.imputation.jobs.practice.entity.Users;
import com.imputation.jobs.utils.MD5Util;
import com.imputation.jobs.utils.ValidateCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author fanshupeng
 * @create 2022/5/6 16:26
 */
@Slf4j
@RestController
@RequestMapping(value = "/prs/hub")
public class EmailController extends BaseController {
    @Autowired
    IMailService iMailService;

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public void sendEmail(@RequestParam("emailAddress") String emailAddress,HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        try {
            ValidateCodeUtil validateCode = new ValidateCodeUtil();
            Users user = new Users();
            user.setEmail("15110151301@163.com");
            user.setPassword(MD5Util.MD5Encode("1qa!QA","utf-8"));
            /**
             * 生成token
             */
            String accessToken = this.getAccessToken(user);
            log.info("token="+accessToken);
            iMailService.sendHtmlMail(emailAddress,"激活链接", accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @RequestMapping(value = "/emailCaptcha", method = RequestMethod.GET)
    public boolean getCheckCaptcha(@RequestParam("code") String code, HttpSession session) {

        try {
            // 获取存放在session域中的验证码  toLowerCase() 不区分大小写进行验证码校验
            String sessionCode= String.valueOf(session.getAttribute("MAILTOKEN")).toLowerCase();
            System.out.println("session里的验证码："+sessionCode);
            String receivedCode=code.toLowerCase();
            System.out.println("用户的验证码："+receivedCode);
            return !sessionCode.equals("") && !receivedCode.equals("") && sessionCode.equals(receivedCode);

        } catch (Exception e) {

            return false;
        }

    }
}
