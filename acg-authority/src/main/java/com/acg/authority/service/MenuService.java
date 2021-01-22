package com.acg.authority.service;

import com.acg.authority.pojo.Menu;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface MenuService {

    //获取全部菜单
    List<Menu> queryAllMenu();

    //根据角色获取菜单
    List<Menu> selectAllMenu(String roleId);

    //给角色分配权限
    void saveRoleMenuRealtionShip(String roleId, String[] menuId);

    //递归删除菜单
    void removeChildById(String id);

    //根据用户id获取用户菜单
    List<String> selectPermissionValueByUserId(String id);

    List<JSONObject> selectPermissionByUserId(String id);


}
