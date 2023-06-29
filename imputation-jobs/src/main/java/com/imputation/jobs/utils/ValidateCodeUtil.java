package com.imputation.jobs.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author fanshupeng
 * @create 2022/5/6 16:20
 */
public class ValidateCodeUtil {
    //Random 不是密码学安全的，加密相关的推荐使用 SecureRandom
    private static Random RANDOM = new SecureRandom();

    private static final String randomString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWSYZ";
    private final static String token = "MAILTOKEN";
    /**
     * 生成6位随机数字
     * @return 返回6位数字验证码
     */
    public String generateVerCode(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        char[] nonceChars = new char[6];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = randomString.charAt(RANDOM.nextInt(randomString.length()));
        }
        //移除之前的session中的验证码信息
        session.removeAttribute(token);
        //将验证码放入session
        session.setAttribute(token,new String(nonceChars));//设置token,参数token是要设置的具体值
        return new String(nonceChars);
    }
}
