package my.dao;

import java.util.List;
import java.util.Map;

import my.entity.Users;

public interface UsersMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);
    
    List<Users> selectUsers(Map<String, Object> paramMap); 
    
    List<Users> selectUsersLogin(Map<String, Object> paramMap);
}