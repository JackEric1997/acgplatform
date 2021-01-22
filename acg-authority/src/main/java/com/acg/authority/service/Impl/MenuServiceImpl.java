package com.acg.authority.service.Impl;

import com.acg.authority.dao.MenuDao;
import com.acg.authority.pojo.Menu;
import com.acg.authority.pojo.User;
import com.acg.authority.service.MenuService;
import com.acg.authority.service.RolePermissionService;
import com.acg.authority.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private RolePermissionService rolePermissionService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private MenuDao menuDao;

    @Override
    public List<Menu> selectAllMenu(String roleId) {
        return null;
    }

    //根据用户id获取用户菜单
    @Override
    public List<String> selectPermissionValueByUserId(String id) {

        List<String> selectMenuValueList = null;
        if(this.isSysAdmin(id)) {
            //如果是系统管理员，获取所有权限
            selectMenuValueList = menuDao.selectAllPermissionValue();
        } else {
            selectMenuValueList = menuDao.selectMenuValueByUserId(id);
        }
        return selectMenuValueList;
    }

    @Override
    public List<JSONObject> selectPermissionByUserId(String id) {
        return null;
    }

    @Override
    public List<Menu> queryAllMenu() {
        return null;
    }

    @Override
    public void removeChildById(String id) {

    }

    @Override
    public void saveRoleMenuRealtionShip(String roleId, String[] permissionId) {

    }

    /**
     * 判断用户是否系统管理员
     * @param id
     * @return
     */
    private boolean isSysAdmin(String id) {
        User user = userService.getById(id);

        if(null != user && "admin".equals(user.getUsername())) {
            return true;
        }
        return false;
    }
}
