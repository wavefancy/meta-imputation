package apache.shiro.config;


import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import my.entity.ResultEnum;
import my.exception.MyException;

public class CustomSessionManager extends DefaultWebSessionManager {
	
	/**
	 * 获取请求头中key为“Authorization”的value == sessionId
	 */
	private static final String AUTHORIZATION ="Authorization";
	
	private static final String REFERENCED_SESSION_ID_SOURCE = "cookie";//"Stateless request";
	
	public CustomSessionManager() {
		super();
	}
	/** 
	 *  @Description shiro框架 自定义session获取方式<br/>
	 *  可自定义session获取规则。这里采用ajax请求头 {@link AUTHORIZATION}携带sessionId的方式
	 */
	@Override
	protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
		// TODO Auto-generated method stub
		String sessionId = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
//		String sessionId = request.getParameter("sessionId");
		System.out.println("~~~~~~~~~~~~~~" + sessionId);
		if (!StringUtils.isEmpty(sessionId)) {
			request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, ShiroHttpServletRequest.COOKIE_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sessionId;
		}
		else {
//			throw new MyException(ResultEnum.LOGIN_REQUIRED);
			System.out.println("~~~~~~~~~~~~~~ no sessionId");
		}
		return super.getSessionId(request, response);
	}
}
