package com.acg.authority.config;

import com.acg.authority.filter.TokenAuthFilter;
import com.acg.authority.filter.TokenLoginFilter;
import com.acg.authority.utils.DefaultPasswordEncoder;
import com.acg.authority.utils.TokenLogoutHandler;
import com.acg.authority.utils.TokenManager;
import com.acg.authority.utils.UnauthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
@EnableWebSecurity
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DefaultPasswordEncoder defaultPasswordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public TokenWebSecurityConfig(UserDetailsService userDetailsService,DefaultPasswordEncoder defaultPasswordEncoder,TokenManager tokenManager,RedisTemplate redisTemplate){
        this.userDetailsService = userDetailsService;
        this.defaultPasswordEncoder = defaultPasswordEncoder;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    //设置退出的地址和token，redis操作地址
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(new UnauthEntryPoint())//没有权限访问
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/login").permitAll()//不进行认证的路径，可以直接访问接口
                .anyRequest().authenticated()
                .and().logout().logoutUrl("/auth/logout")//退出路径
                .addLogoutHandler(new TokenLogoutHandler(tokenManager,redisTemplate)).and()
                .addFilter(new TokenLoginFilter(authenticationManager(),tokenManager,redisTemplate))
                .addFilter(new TokenAuthFilter(authenticationManager(),tokenManager,redisTemplate)).httpBasic();
        /*http.exceptionHandling()
                .authenticationEntryPoint(new UnauthEntryPoint())
                .and().csrf().disable()
                .authorizeRequests(req -> req.anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/auth/login").usernameParameter("username").passwordParameter("password"))
                .logout().logoutUrl("/auth/logout")//退出路径
                .addLogoutHandler(new TokenLogoutHandler(tokenManager,redisTemplate)).and()
                .addFilter(new TokenLoginFilter(authenticationManager(),tokenManager,redisTemplate))
                .addFilter(new TokenAuthFilter(authenticationManager(),tokenManager,redisTemplate)).httpBasic();*/


    }

    //调用userDetailsService和密码处理
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }

    //不进行认证的路径，可以直接访问,访问资源用这个
    /*@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/index.html", "/img/**", "/fonts/**", "/favicon.ico", "/verifyCode");
    }*/


}
