package com.acg.authority.service;

import com.acg.authority.pojo.User;
import com.acg.authority.pojo.vo.RegisterVo;
import com.acg.common.utils.R;

public interface UserService {

    R registerUser(RegisterVo registerVo);

    User selectByUsername(String username);

    User getById(String id);

}
