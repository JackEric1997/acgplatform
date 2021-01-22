package com.acg.authority.dao;

import com.acg.authority.pojo.User;
import com.acg.authority.pojo.vo.RegisterVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {

    List<User> registerUser(RegisterVo registerVo);

    void addUser(User user);

    User selectByUsername(String username);

}
