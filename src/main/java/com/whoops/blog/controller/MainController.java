package com.whoops.blog.controller;

import com.whoops.blog.pojo.Authority;
import com.whoops.blog.pojo.User;
import com.whoops.blog.service.AuthorityServiceImpl;
import com.whoops.blog.service.UserServiceImpl;
import com.whoops.blog.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 主控制器
 */
@Controller
public class MainController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private AuthorityServiceImpl authorityService;

    /**
     * 访问根目录
     * @return
     */
    @GetMapping("/")
    public String root(){
        return "redirect:/index";
    }
    /**
     * 访问index页面
     * @return
     */
    @GetMapping("/index")
    public String index(){
        return "redirect:/blogs";
    }
    /**
     * 访问login页面
     * @return
     */
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    /**
     * 登录失败,重定向到login页面
     * @return
     */
    @GetMapping("/login-error")
    public String loginError(Model model){
        model.addAttribute("loginError",true);
        model.addAttribute("errorMsg","登陆失败，用户名或密码错误！");
        return "login";
    }
    /**
     * 访问注册页面
     * @return
     */
    @GetMapping("/register")
    public String register(){
        return "register";
    }

    /**
     * 注册用户
     * @return
     */
    @PostMapping("/register")
    public String register(User user){
        List<Authority> authorityList = new ArrayList<>();
        authorityList.add(authorityService.getAuthById(Constants.ROLE_USER_AUTHORITY_ID));
        user.setAuthorityList(authorityList);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        userService.saveOrUpdateUser(user);
        return "redirect:/login";
    }
}

































