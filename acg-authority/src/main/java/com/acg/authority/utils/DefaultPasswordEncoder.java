package com.acg.authority.utils;

import com.acg.common.utils.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    public DefaultPasswordEncoder(){
        this(-1);
    }

    public DefaultPasswordEncoder(int strength){}

    //进行BCrypt加密
    @Override
    public String encode(CharSequence charSequence) {
        return BCrypt.encrypt(charSequence.toString());
    }

    //进行密码比对
    @Override
    public boolean matches(CharSequence charSequence, String encodedPassword) {
        return BCrypt.match(charSequence.toString(),encodedPassword);
    }
}
