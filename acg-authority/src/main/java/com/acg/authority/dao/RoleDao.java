package com.acg.authority.dao;

import com.acg.authority.pojo.Role;
import com.acg.authority.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleDao{
    //查询所有的角色
    List<Role> allRolesList();
    //根据角色id查用户
    List<Role> existRoleList(List<String> roleIdList);
}
