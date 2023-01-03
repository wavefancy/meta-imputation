package my.dao;

import my.entity.RolesPermissions;

public interface RolesPermissionsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RolesPermissions record);

    int insertSelective(RolesPermissions record);

    RolesPermissions selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RolesPermissions record);

    int updateByPrimaryKey(RolesPermissions record);
}