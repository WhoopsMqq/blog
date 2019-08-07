package com.whoops.blog.config;

import com.whoops.blog.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//启用方法级别的安全隔离
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String KEY = "whoops";

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();//使用Bcrypt加密
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    //1.要重写configure方法,自定义配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**","/js/**","/font/**","/index").permitAll() //无论是谁都可以访问
                .antMatchers("/h2-console/**").permitAll()//都可以访问
                .antMatchers("/admins/**").hasRole("ADMIN")//需要ADMIN角色才能访问
                .and()
                .formLogin() //基于表单的验证方式
                .loginPage("/login")//自定义的表单验证
                .failureUrl("/login-error")//验证失败重定向页面
                .and().rememberMe().key(KEY)//启用remember me
                .and().exceptionHandling().accessDeniedPage("/403");//异常处理拒绝访问就重定向到403页面
        http.csrf().ignoringAntMatchers("/h2-console/**");//禁用h2控制台的CSRF防护
        http.headers().frameOptions().sameOrigin();//允许来自同一来源的h2控制台的请求
    }
    //csrf : 跨站伪造防御
    //2.将认证管理注入进来
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userService);
        auth.authenticationProvider(authenticationProvider());
    }

}



















