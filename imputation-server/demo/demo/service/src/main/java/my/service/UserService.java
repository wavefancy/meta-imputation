package my.service;

import my.entity.Users;

public interface UserService {
	public long userRegister(Users user, String roleName) throws Exception;
	
	public long userUpdate(Users users) throws Exception;
	
	public Users queryThisUser() throws Exception;
	
	public void userLogin(Users user) throws Exception;
	
	public void userLogout();
	
	public String requireRoles(String role) throws Exception;
}
