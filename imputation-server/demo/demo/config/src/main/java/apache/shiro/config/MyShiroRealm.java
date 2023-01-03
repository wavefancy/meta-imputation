package apache.shiro.config;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;

public class MyShiroRealm extends JdbcRealm {
	MyShiroRealm() {
		super();
	}
	
	/*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        return super.doGetAuthenticationInfo(token);
    } 
}


