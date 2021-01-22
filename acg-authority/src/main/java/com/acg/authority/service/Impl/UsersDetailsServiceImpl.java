package com.acg.authority.service.Impl;

import com.acg.authority.pojo.SecurityUser;
import com.acg.authority.pojo.User;
import com.acg.authority.service.MenuService;
import com.acg.authority.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("userDetailsService")
public class UsersDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Resource
    private MenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String e) throws UsernameNotFoundException {

        User user = userService.selectByUsername(e);
        if (user == null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        //根据用户查询用户权限列表
        List<String> menuValueList= menuService.selectPermissionValueByUserId(user.getId());
        SecurityUser securityUser = new SecurityUser();
        securityUser.setCurrentUserInfo(user);
        securityUser.setPermissionValueList(menuValueList);
        return securityUser;
    }
}
