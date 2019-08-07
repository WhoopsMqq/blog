package com.whoops.blog.controller;

import com.whoops.blog.pojo.Authority;
import com.whoops.blog.pojo.User;
import com.whoops.blog.service.AuthorityServiceImpl;
import com.whoops.blog.service.UserServiceImpl;
import com.whoops.blog.util.ConstraintViolationExceptionHandler;
import com.whoops.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private AuthorityServiceImpl authorityService;

    /**
     * 得到全部用户
     */
    @GetMapping
    public ModelAndView list(@RequestParam(value="async",required=false) boolean async,
                             @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
                             @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
                             @RequestParam(value="name",required=false,defaultValue="") String name,
                             Model model) {
        Pageable pageable = new PageRequest(pageIndex,pageSize);
        Page<User> page = userService.listUsersByNameLike(name,pageable);
        List<User> list = page.getContent();
        model.addAttribute("page", page);
        model.addAttribute("userList",list);
        model.addAttribute("title","用户管理");
        return new ModelAndView("users/list","userModel",model);
    }

    /**
     * 管理员添加用户
     */
    @PostMapping
    public ResponseEntity<Response> createUser(User user,Long authorityId){
        Authority authority = authorityService.getAuthById(authorityId);
        List<Authority> authorityList = new ArrayList<>();
        authorityList.add(authority);

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if(user.getId() != null){
            User oldUser = userService.getUserById(user.getId());
            user.setAvatar(oldUser.getAvatar());
            if(!StringUtils.equals(oldUser.getPassword(),encoder.encode(user.getPassword()))){
                user.setPassword(encoder.encode(user.getPassword()));
            }
        }
        user.setAuthorityList(authorityList);

        try {
            userService.saveOrUpdateUser(user);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        }
        return ResponseEntity.ok().body(new Response(true,"用户保存成功!"));
    }

    /**
     * 进入管理员添加用户界面
     */
    @GetMapping("/add")
    public ModelAndView addUser(Model model){
        model.addAttribute("user",new User(null,null,null,null));
        return new ModelAndView("/users/add","userModel",model);
    }

    /**
     * 管理员修改用户
     */
    @GetMapping(value = "/edit/{id}")
    public ModelAndView editUser(@PathVariable("id") Long id,Model model){
        User user = userService.getUserById(id);
        model.addAttribute("user",user);
        return new ModelAndView("/users/edit","userModel",model);
    }

    /**
     * 管理员删除账户
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response> delUser(@PathVariable("id") Long id){
        try {
            userService.removeUser(id);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false,ConstraintViolationExceptionHandler.getMessage(e)));
        }
        return ResponseEntity.ok().body(new Response(true,"删除成功!"));
    }






}
