package interceptor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import my_interceptor.OneInterceptor;
import my_interceptor.TwoInterceptor;

/**
 * <br>
 * 标题: 自定义配置类<br>
 * 描述: 注册自定义的拦截器<br>
 *
 * @author zc
 * @date 2018/04/26
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 拦截器按照顺序执行
         */
        registry.addInterceptor(new OneInterceptor()).addPathPatterns("/*/**");
  //    registry.addInterceptor(new TwoInterceptor()).addPathPatterns("/*/**").addPathPatterns("/insert");
  //    registry.addInterceptor(new TwoInterceptor()).addPathPatterns("/two/**").addPathPatterns("/one/**");
        super.addInterceptors(registry);
    }
}
