package apache.shiro.config;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {
	
	@Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("defaultSecurityManager") DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager((org.apache.shiro.mgt.SecurityManager) securityManager);
        shiroFilterFactoryBean.setLoginUrl("/user/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/notRole");
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filterChainDefinitionMap.put("/user/register", "anon");
        filterChainDefinitionMap.put("/user/login", "anon");
   //     filterChainDefinitionMap.put("/user/logout", "authc");
        
        filterChainDefinitionMap.put("/admin/**", "authc");
        filterChainDefinitionMap.put("/user/**", "authc"); 
        filterChainDefinitionMap.put("/file/**", "authc");
        //主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截 剩余的都需要认证
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

	@Bean("sessionManager")
    public SessionManager sessionManager(){
        CustomSessionManager manager = new CustomSessionManager();
		/*使用了shiro自带缓存，
		如果设置 redis为缓存需要重写CacheManager（其中需要重写Cache）
		manager.setCacheManager(this.RedisCacheManager());*/

        manager.setSessionDAO(new EnterpriseCacheSessionDAO());
        manager.setGlobalSessionTimeout(-1);
        return manager;
    }
    
    @Bean
    public MyShiroRealm myShiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
  //    myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myShiroRealm;
    }
    
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }
    
    @Bean("defaultSecurityManager")
    public DefaultWebSecurityManager securityManager(@Qualifier("mysqlDataSource") DataSource dataSource) {
    	DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
    	MyShiroRealm myShiroRealm = new MyShiroRealm();
    	myShiroRealm.setDataSource(dataSource); 
        manager.setRealm(myShiroRealm);//往SecurityManager中注入Realm，代替原本的默认配置
        manager.setSessionManager(sessionManager());
        
        SecurityUtils.setSecurityManager(manager);
        return manager;
    }

}
