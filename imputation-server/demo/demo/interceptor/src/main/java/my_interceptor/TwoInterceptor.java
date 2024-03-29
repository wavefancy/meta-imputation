package my_interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * <br>
 * 标题: 自定义拦截器二<br>
 * 描述: 拦截器<br>
 *
 * @author zc
 * @date 2018/04/26
 */
public class TwoInterceptor implements HandlerInterceptor  {

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
  //      if (true) {
   //         returnErrorResponse(response, JsonResult.errMsg("被two拦截"));
  //      }
        System.out.println("被two拦截...");
        return false;
    }
    
    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response
    		, Object object, ModelAndView mv)
            throws Exception {
    }
    
    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception ex)
            throws Exception {
    }
    /*
    public void returnErrorResponse(HttpServletResponse response, JsonResult result) throws Exception{
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }*/
}
