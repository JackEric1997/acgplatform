package com.acg.authority.dao;

import com.acg.authority.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleDao {

    //根据用户id，查询用户拥有的角色id
    List<UserRole> existUserRoleList(String userId);

}
