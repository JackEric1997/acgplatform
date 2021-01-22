package com.acg.authority.service;

import com.acg.authority.pojo.UserRole;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
public interface UserRoleService{

    //根据用户id拥有的角色id
    List<UserRole> existUserRoleList(String userid);
}
