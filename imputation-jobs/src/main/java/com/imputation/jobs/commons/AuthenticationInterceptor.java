package com.imputation.jobs.commons;

import com.alibaba.fastjson.JSON;
import com.imputation.jobs.authentication.dto.UserReqDTO;
import com.imputation.jobs.authentication.service.AuthService;
import com.imputation.jobs.constant.CurrentUserConstants;
import com.imputation.jobs.practice.entity.Users;
import com.imputation.jobs.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校验是否登录过滤器
 */
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {
    public final static String ACCESS_TOKEN = "accessToken";

    @Autowired
    private AuthService authService;

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 判断接口是否需要登录
        Authorization methodAnnotation = method.getAnnotation(Authorization.class);
        // 没有 @Authorization 注解，不需要认证
        if (methodAnnotation != null){
            // 有 @Authorization 注解，需要认证
            // 判断是否存在令牌信息，如果存在，则允许登录
//            String accessToken = request.getParameter(ACCESS_TOKEN);
            String accessToken = request.getHeader(ACCESS_TOKEN);
            log.info("验证登陆状态开始accessToken="+accessToken);
            if (null == accessToken) {
                returnJson(response);
                return false;
            }
            //验证token是否正确，是否过期 过期时间半小时，token有效返回true
            if(!TokenUtil.verify(accessToken)){
                returnJson(response);
                return false;
            };

            //解析token
            List<String> keyList = new ArrayList<>();
            keyList.add("email");
            keyList.add("password");
            Map<String, String> resMap = TokenUtil.getMessage(accessToken,keyList);
            log.info("解析token="+JSON.toJSONString(resMap));

            //校验用户是否存在
            Users user = new Users();
            user.setEmail(resMap.get("email"));
            user.setPassword(resMap.get("password"));
            log.info("登录过滤器调用authService的getUserInfo方法开始，user="+ JSON.toJSONString(user));
            BaseResult userInfo = authService.getUserInfo(user);
            log.info("登录过滤器调用authService的getUserInfo方法结束，userInfo="+JSON.toJSONString(userInfo));
            Users userRes= (Users)userInfo.getData();
            if(userRes == null){
                log.info("用户不存在，请重新登录");
                returnJson(response);
                return false;
            }
            UserReqDTO userReqDTO = new UserReqDTO();
            userReqDTO.setEmail(userRes.getEmail());
            userReqDTO.setPassword(userRes.getPassword());
            userReqDTO.setFirstName(userRes.getFirstName());
            userReqDTO.setLastName(userRes.getLastName());
            userReqDTO.setJobTitle(userRes.getJobTitle());
            userReqDTO.setOrganisation(userRes.getOrganisation());
            userReqDTO.setCity(userRes.getCity());
            userReqDTO.setCountry(userRes.getCountry());
            if(userRes.getAge() != null){
                userReqDTO.setAge(userRes.getAge().toString());
            }
            userReqDTO.setId(userRes.getId().toString());
            userReqDTO.setIdNo(userRes.getIdNo());
            if(userRes.getSex() != null){
                userReqDTO.setSex(userRes.getSex().toString());
            }
            userReqDTO.setMobile(userRes.getMobile());
            // 当前登录用户@CurrentUser
            request.setAttribute(CurrentUserConstants.CURRENT_USER, userReqDTO);
            return true;
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
    /**
     * 拦截返回处理
     */
    private void returnJson(HttpServletResponse response){
        //重置response
        response.reset();
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        //解决跨域问题
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.addHeader("Access-Control-Max-Age", "3600");
        try {
            writer = response.getWriter();
            Map<String, Object> result = new HashMap<>();
            result.put("code", "400");
            result.put("msg", "用户未登录或令牌token无效");
            writer.write(JSON.toJSONString(result));
            writer.flush();
        } catch (IOException e){
            log.error(AuthenticationInterceptor.class+"拦截器输出流异常",e);
        } finally {
            if(writer != null){
                writer.close();
            }
        }
    }
}
