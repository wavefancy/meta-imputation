package my.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import my.dao.UserRolesMapper;
import my.dao.UsersMapper;
import my.entity.ResultEnum;
import my.entity.UserRoles;
import my.entity.Users;
import my.exception.MyException;
import my.util.ServiceInvokeUtil;
import my.util.SomeMethod;

@Service
public class UserServiceIml implements UserService {
	
	@Autowired
	private UsersMapper usersMapper;
	
	@Autowired
	private UserRolesMapper userRolesMapper;
	
	@Transactional(value = "mysqlTransactionManager")
	@Override
	public long userRegister(Users users, String roleName) throws Exception {
		String username = users.getUsername();
        String password = users.getPassword();
        String email = users.getEmail();

        isUserExisting(username, email);
        String[] saltAndCiphertext = encryptPassword(password);
        users.setPasswordSalt(saltAndCiphertext[0]);
        users.setPassword(saltAndCiphertext[1]);
        users.setUserStatus("Registered");
        
        ServiceInvokeUtil.call(() -> usersMapper.insert(users), ResultEnum.DATABASE_FAILURE, users);
        UserRoles userRoles = new UserRoles();
        userRoles.setUsername(users.getUsername());
        userRoles.setRoleName(roleName);
        ServiceInvokeUtil.call(() -> userRolesMapper.insert(userRoles), ResultEnum.DATABASE_FAILURE, userRoles);
		return users.getId();
	}	
	
	/**
	 * 用户注册时加密用户的密码 输入密码明文 返回密文与盐值
	 * 
	 * @param password
	 * @return 第一个是密文 第二个是盐值
	 */
	private String[] encryptPassword(String password) {
		String salt = new SecureRandomNumberGenerator().nextBytes().toHex(); // 生成盐值
		String ciphertext = new Md5Hash(password, salt, 3).toString(); // 生成的密文
		String[] strings = new String[] {salt, ciphertext};
		return strings;
	}
	
	/**
	 * 判断用户名称和邮箱是否注册过
	 * 
	 * 
	 */
	private boolean isUserExisting(String username, String email) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
        List<Users> userL = null;
        
        paramMap.put("username", username);
        userL = ServiceInvokeUtil.call(() -> usersMapper.selectUsers(paramMap), ResultEnum.DATABASE_FAILURE, paramMap);
        if(!CollectionUtils.isEmpty(userL)) {
			throw new MyException(ResultEnum.REGISTER_FAILURE1);
		}
        
        paramMap.remove("username");
        paramMap.put("email", email);
        userL = ServiceInvokeUtil.call(() -> usersMapper.selectUsers(paramMap), ResultEnum.DATABASE_FAILURE, paramMap);
        if(!CollectionUtils.isEmpty(userL)) {
			throw new MyException(ResultEnum.REGISTER_FAILURE3);
		}
		return false;
	}
	
	@Override
	public String requireRoles(String role) throws Exception {	
        Subject subject = JdbcSubject();
        if(!subject.isAuthenticated())
        	return null;
        try {
        	subject.checkRole(role);
        }
        catch (Exception e) {

			throw new MyException(ResultEnum.ACCESS_FAILURE);
		}
        String username = subject.getPrincipal().toString();
        return username;
	}
	
	@Transactional(value = "mysqlTransactionManager")
	@Override
	public long userUpdate(Users users) throws Exception {
        Users euser = queryThisUser();
	    String password = users.getPassword();
        if(password != null && !password.contentEquals("")) {
	        String[] saltAndCiphertext = encryptPassword(password);
	        users.setPasswordSalt(saltAndCiphertext[0]);
	        users.setPassword(saltAndCiphertext[1]);
        }
        users.setUsername(euser.getUsername());
        users.setId(euser.getId());
        ServiceInvokeUtil.call(() -> usersMapper.updateByPrimaryKeySelective(users), ResultEnum.DATABASE_FAILURE, users);
//      bjcalogMapper.insert(SomeMethod.createLog(users.getVendorName() + "修改服务商信息成功", "课题5"));
		return users.getId();
	}	
	
	@Override
	public Users queryThisUser() throws Exception {
		String username = SomeMethod.getUsername();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("username", username);
		List<Users> euser = ServiceInvokeUtil.call(() -> usersMapper.selectUsers(paramMap), ResultEnum.DATABASE_FAILURE, paramMap);
        
		if(CollectionUtils.isEmpty(euser)) {
			throw new MyException(ResultEnum.USERQUERY_FAILURE);
		}
		return euser.get(0);
	}
	
	/**
	 * 用户登录函数，在controller里调用
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@Override
	public void userLogin(Users users) throws Exception {	
        Subject subject = JdbcSubject();
        if(subject.isAuthenticated())
        	return;
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("username", users.getUsername());
        paramMap.put("userStatus", "Registered");
        List<Users> euserlist = ServiceInvokeUtil.call(() -> usersMapper.selectUsersLogin(paramMap), ResultEnum.DATABASE_FAILURE, paramMap);	
        if(CollectionUtils.isEmpty(euserlist)) {
			throw new MyException(ResultEnum.LOGIN_FAILURE);
		}	
        
        Users euser = euserlist.get(0);     
        String epassword = getInputPasswordCiph(users.getPassword(), euser.getPasswordSalt());	
        UsernamePasswordToken token = new UsernamePasswordToken(users.getUsername(), epassword); 
        
        try {
        	if(!subject.isAuthenticated()) {
        		subject.login(token); // login()方法会调用 Realm类中执行认证逻辑的方法，并将这个参数传递给doGetAuthenticationInfo()方法
        	}
        	return;
		} 
		catch(UnknownAccountException e) {// 抛出这个异常说明用户不存在	
			throw new MyException(ResultEnum.LOGIN_FAILURE);
		} 
		catch(IncorrectCredentialsException e) {// 抛出这个异常说明密码错误
			throw new MyException(ResultEnum.LOGIN_FAILURE);
		}
        catch(Exception e) {
			throw new MyException(ResultEnum.LOGIN_FAILURE);
		}
	}
	
	private Subject JdbcSubject() {
		Subject subject = SecurityUtils.getSubject();
		return subject;
	}
	
	/**
	 * 获得本次输入的密码的密文
	 * 
	 * @param password
	 * @param salt
	 * @return
	 */
	private String getInputPasswordCiph(String password, String salt) {
		if (salt == null) {
			password = "";
		}
		String ciphertext = new Md5Hash(password, salt, 3).toString(); // 生成的密文
		return ciphertext;
	}
	
	@Override
	public void userLogout() {
		Subject subject = SecurityUtils.getSubject();// 取出当前验证主体
		if (subject != null) {
			subject.logout();// 不为空，执行一次logout的操作，将session全部清空
		}
	}

}
