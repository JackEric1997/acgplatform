package com.acg.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCrypt {

    public static String encrypt(String pswd) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String hashPass = bCryptPasswordEncoder.encode(pswd);
        return hashPass;
    }

    public static boolean match(String pswd,String encodedPswd){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean result = bCryptPasswordEncoder.matches(pswd,encodedPswd);
        return result;
    }
}
