package com.acg.authority.controller;

import com.acg.authority.pojo.User;
import com.acg.authority.pojo.vo.RegisterVo;
import com.acg.authority.service.UserService;
import com.acg.common.utils.BCrypt;
import com.acg.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("注册")
    @PostMapping("/register")
    public R registerUser(@RequestBody RegisterVo registerVo){
        return userService.registerUser(registerVo);
    }

    @ApiOperation("")
    @GetMapping("/get")
    public String test(){

        return null;
    }
}
