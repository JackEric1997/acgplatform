package com.acg.authority.service.Impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.acg.authority.dao.UserDao;
import com.acg.authority.pojo.User;
import com.acg.authority.pojo.vo.RegisterVo;
import com.acg.authority.service.UserService;
import com.acg.authority.utils.IdGeneratorSnowflake;
import com.acg.common.entity.StatusCode;
import com.acg.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IdGeneratorSnowflake idGenerator;

    //注册
    @Override
    public R registerUser(RegisterVo registerVo) {
        //获取注册的数据
        String code = registerVo.getCode(); //验证码
        String email = registerVo.getEmail(); //邮箱
        String nickname = registerVo.getNickname(); //昵称
        String password = registerVo.getPassword(); //密码
        int sex = registerVo.getSex();//性别
        //非空判断
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password) || StringUtils.isEmpty(code) || StringUtils.isEmpty(nickname)) {
            return new R(false, StatusCode.ERROR,"注册失败");
        }
        //判断验证码,获取redis验证码
        String redisCode = redisTemplate.opsForValue().get(email);
        if(!code.equals(redisCode)) {
            return new R(false, StatusCode.ERROR,"注册失败");
        }
        //判断邮箱和昵称是否重复，表里面存在相同邮箱或昵称不进行添加
        List<User> userList = userDao.registerUser(registerVo);
        if (userList != null){
            boolean isNickName = userList.stream().anyMatch(item -> item.getNickName().equals(nickname));
            boolean isEmail = userList.stream().anyMatch(item -> item.getEmail().equals(email));
            if (isNickName && isEmail){
                return new R(false, StatusCode.ERROR,"用户名和邮箱重复");
            }else if (isNickName){
                return new R(false, StatusCode.ERROR,"用户名重复");
            }else if (isEmail){
                return new R(false, StatusCode.ERROR,"邮箱重复");
            }
        }
        //数据添加数据库中
        User user = new User();
        Date time = DateUtil.date();
        user.setId(Convert.toStr(idGenerator.snowflakeId()));
        user.setPassword(password);
        user.setNickName(nickname);
        user.setSex(sex);
        user.setEmail(email);
        user.setGmtModified(time);
        user.setGmtCreate(time);
        userDao.addUser(user);
        return new R(true,StatusCode.OK,"注册成功");
    }

    @Override
    public User selectByUsername(String username) {
        return userDao.selectByUsername(username);
    }

    @Override
    public User getById(String id) {
        return null;
    }

}
