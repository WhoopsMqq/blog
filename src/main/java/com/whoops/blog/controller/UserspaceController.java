package com.whoops.blog.controller;

import com.whoops.blog.pojo.Blog;
import com.whoops.blog.pojo.Catalog;
import com.whoops.blog.pojo.User;
import com.whoops.blog.pojo.Vote;
import com.whoops.blog.service.BlogServiceImpl;
import com.whoops.blog.service.CatalogServiceImpl;
import com.whoops.blog.service.UserServiceImpl;
import com.whoops.blog.util.ConstraintViolationExceptionHandler;
import com.whoops.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 用户主页控制器
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private BlogServiceImpl blogService;
    @Autowired
    private CatalogServiceImpl catalogService;

    /**
     * 根据用户名称进入用户主页
     */
    @GetMapping("/{username}")
    public String userSpace(@PathVariable("username") String username, Model model){
        User user = userService.findUserByUsername(username);
        model.addAttribute("user",user);
        return "redirect:/u/"+username+"/blogs";
    }


    /**
     * 进入用户个人设置页面
     */
    @GetMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")//用户权限认证,用户只能修改自己的信息
    public ModelAndView  profile(@PathVariable("username") String username,Model model){
        User user = userService.findUserByUsername(username);
        model.addAttribute("user",user);
        return new ModelAndView("/userspace/profile","userModel",model);
    }

    /**
     * 保存跟个人设置
     */
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public String editProfile(@PathVariable("username") String username,User user){
        User orangUser = userService.getUserById(user.getId());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!StringUtils.equals(orangUser.getPassword(),encoder.encode(user.getPassword()))){
            orangUser.setPassword(encoder.encode(user.getPassword()));
        }
        orangUser.setEmail(user.getEmail());
        orangUser.setName(user.getName());
        userService.saveOrUpdateUser(orangUser);
        return "redirect:/u/"+user.getUsername()+"/profile";
    }

    /**
     * 进入头像修改界面
     */
    @GetMapping("/{username}/avatar")
    public ModelAndView avatar(@PathVariable("username") String username,Model model){
        User user = userService.findUserByUsername(username);
        model.addAttribute("user",user);
        return new ModelAndView("/userspace/avatar","userModel",model);
    }

    /**
     * 保存用户头像
     */
    @PostMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveAvatar(@PathVariable("username") String username, @RequestBody User user) {
        String avatarUrl = user.getAvatar();
        User originalUser = userService.getUserById(user.getId());
        originalUser.setAvatar(avatarUrl);
        userService.saveOrUpdateUser(originalUser);
        return ResponseEntity.ok().body(new Response(true, "头像跟换成功!", avatarUrl));
    }

    /**
     * 列出用户的博客
     */
    @GetMapping("/{username}/blogs")
    public String listBlogsByUserOrder(@PathVariable(value = "username")String username,
                                       @RequestParam(value = "order",required = false,defaultValue = "new")String order,//最新或最热查询
                                       @RequestParam(value = "catalogId",required = false)Long catalogId,//根据分类查询
                                       @RequestParam(value = "keyword",required = false,defaultValue = "")String keyword,//关键字 -> title
                                       @RequestParam(value = "async",required = false)boolean async,
                                       @RequestParam(value = "pageIndex",required = false,defaultValue = "0")int pageIndex,//分页
                                       @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,Model model){
        Page<Blog> page = null;
        User user = userService.findUserByUsername(username);
        if(catalogId != null && catalogId > 0){
            Pageable pageable = new PageRequest(pageIndex,pageSize);
            Catalog catalog = catalogService.getCatalogById(catalogId);
            page = blogService.findByCatalog(catalog,pageable);
        }else if(StringUtils.equals(order,"new")){
            Pageable pageable = new PageRequest(pageIndex,pageSize);
            page = blogService.listBlogByUserAndTitleLikeVote(user,keyword,pageable);
        }else if(StringUtils.equals(order,"hot")){
            Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","voteSize");
            Pageable pageable = new PageRequest(pageIndex,pageSize,sort);
            page = blogService.listBlogByUserAndTitleLikeVoteAndSort(user,keyword,pageable);
        }

        model.addAttribute("blogList",page.getContent());
        model.addAttribute("user",user);
        model.addAttribute("order",order);
        model.addAttribute("catalogId",catalogId);
        model.addAttribute("keyword",keyword);
        model.addAttribute("page",page);

        return (async == true ? "/userspace/u :: #mainContainerRepleace":"/userspace/u");
    }

    /**
     * 根据id查看用户的博客
     */
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username")String username,@PathVariable("id")Long id,Model model){
        boolean isBlogOwner = false;

        User principal = null;
        //判断是否本人访问
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null && username.equals(principal.getUsername())) {
                isBlogOwner = true;
            }
        }

        if(!isBlogOwner){//非本人访问,阅读量+1
            blogService.readingIncrease(id);
        }

        Blog blog = blogService.getBlogById(id);

        Vote currentVote = null;
        if (principal != null){
            List<Vote> voteList = blog.getVoteList();
            for(Vote v:voteList){
                if(StringUtils.equals(v.getUser().getUsername(),principal.getUsername())){
                    currentVote = v;
                    break;
                }
            }
        }
        model.addAttribute("currentVote",currentVote);
        model.addAttribute("blogModel",blog);
        model.addAttribute("isBlogOwner",isBlogOwner);

        return "/userspace/blog";
    }

    /**
     * 进入添加博客界面
     */
    @GetMapping("/{username}/blogs/edit")
    public ModelAndView createBlog(@PathVariable("username")String username, Model model){
        Blog blog = new Blog(null,null,null);
        User user = userService.findUserByUsername(username);
        List<Catalog> catalogList = catalogService.getCatalogsByUser(user);
        model.addAttribute("catalogs",catalogList);
        model.addAttribute("blog",blog);
        return new ModelAndView("/userspace/blogedit","blogModel",model);
    }

    /**
     * 进入博客修改界面
     */
    @GetMapping("/{username}/blogs/edit/{id}")
    public ModelAndView editBlog(@PathVariable("username")String username,@PathVariable("id")Long id, Model model){
        Blog blog = blogService.getBlogById(id);
        User user = userService.findUserByUsername(username);
        List<Catalog> catalogList = catalogService.getCatalogsByUser(user);
        model.addAttribute("catalogs",catalogList);
        model.addAttribute("blog",blog);
        return new ModelAndView("/userspace/blogedit","blogModel",model);
    }

    /**
     * 根据id删除博客
     */
    @DeleteMapping("{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> delBlog(@PathVariable("id")Long id,@PathVariable("username")String username){
        try {
            blogService.delBlog(id);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        }
        String returnUrl = "/u/"+username+"/blogs";
        return ResponseEntity.ok().body(new Response(true,"博客删除成功!",returnUrl));
    }

    @PostMapping("{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveBlog(Model model,@PathVariable("username")String username,@RequestBody Blog blog){
        if(blog.getCatalog().getId() == null){
            return ResponseEntity.ok().body(new Response(false,"未选择分类"));
        }

        String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        try {
            if (blog.getId() != null && blog.getId() != 0) {
                Blog orignalBlog = blogService.getBlogById(blog.getId());
                orignalBlog.setTitle(blog.getTitle());
                orignalBlog.setContent(blog.getContent());
                orignalBlog.setSummary(blog.getSummary());
                orignalBlog.setCatalog(blog.getCatalog());
                orignalBlog.setTags(blog.getTags());
                blogService.saveBlog(orignalBlog);
                redirectUrl = "/u/" + username + "/blogs/" + orignalBlog.getId();
            } else {
                User user = userService.findUserByUsername(username);
                blog.setUser(user);
                blogService.saveBlog(blog);
                redirectUrl = "/u/" + username + "/blogs/";
            }
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false,ConstraintViolationExceptionHandler.getMessage(e)));
        }
        return ResponseEntity.ok().body(new Response(true,"保存成功!",redirectUrl));
    }



}






















































