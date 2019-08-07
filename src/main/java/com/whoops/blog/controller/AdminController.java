package com.whoops.blog.controller;

import com.whoops.blog.vo.Menu;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台管理控制器
 */
@Controller
@RequestMapping("/admins")
public class AdminController {


    /**
     * 得到用户 返回用户信息
     */
    @GetMapping
    public ModelAndView listUser(Model model){
        List<Menu> list = new ArrayList<>();
        list.add(new Menu("用户管理","/users"));
        model.addAttribute("list",list);
        return new ModelAndView("/admins/index","model",model);
    }

}
