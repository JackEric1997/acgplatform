package com.acg.authority.service.Impl;

import com.acg.authority.dao.UserRoleDao;
import com.acg.authority.pojo.UserRole;
import com.acg.authority.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public List<UserRole> existUserRoleList(String userid) {
        return userRoleDao.existUserRoleList(userid);
    }
}
