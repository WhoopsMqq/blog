package com.whoops.blog.controller;

import com.whoops.blog.pojo.Blog;
import com.whoops.blog.pojo.Catalog;
import com.whoops.blog.pojo.User;
import com.whoops.blog.service.BlogServiceImpl;
import com.whoops.blog.service.CatalogServiceImpl;
import com.whoops.blog.service.UserServiceImpl;
import com.whoops.blog.util.ConstraintViolationExceptionHandler;
import com.whoops.blog.vo.CatalogVo;
import com.whoops.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Controller
@RequestMapping("/catalogs")
public class CatalogController {
    @Autowired
    private CatalogServiceImpl catalogService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BlogServiceImpl blogService;

    /**
     * 获取分类列表
     */
    @GetMapping
    public String listCatalogs(@RequestParam(name = "username",required = false)String username, Model model){
        User user = userService.findUserByUsername(username);
        List<Catalog> catalogList = catalogService.getCatalogsByUser(user);

        boolean isOwner = false;

        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }

        model.addAttribute("catalogs",catalogList);
        model.addAttribute("isCatalogsOwner",isOwner);
        return "/userspace/u :: #catalogRepleace";
    }

    /**
     * 创建分类
     */
    @PostMapping
    @PreAuthorize("authentication.name.equals(#catalogVo.username)")
    public ResponseEntity<Response> saveCatalog(@RequestBody CatalogVo catalogVo){
        Catalog catalog = catalogVo.getCatalog();
        User user = userService.findUserByUsername(catalogVo.getUsername());
        try{
            catalog.setUser(user);
            catalogService.saveCatalog(catalog);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        }catch (Exception e){
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "保存成功!",null));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{catalogId}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> delCatalogById(@PathVariable("catalogId")Long catalogId,@RequestParam("username")String username){
        try{
            Catalog catalog = catalogService.getCatalogById(catalogId);
            List<Blog> blogList = blogService.findByCatalog(catalog);
            if(blogList != null && blogList.size() >0){
                return ResponseEntity.ok().body(new Response(false, "该分类下有博客,无法删除"));
            }
            catalogService.removeCatalog(catalogId);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        }catch (Exception e){
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "删除成功!",null));
    }

    /**
     * 获取分类编辑页面
     */
    @GetMapping("/edit")
    public String getCatalogEdit(Model model){
        Catalog catalog = new Catalog(null,null);
        model.addAttribute("catalog",catalog);
        return "/userspace/catalogedit";
    }

    /**
     * 根据Id获取分类页面
     */
    @GetMapping("/edit/{id}")
    public String getCatalogEditById(@PathVariable("id")Long catalogId,Model model){
        Catalog catalog = catalogService.getCatalogById(catalogId);
        model.addAttribute("catalog",catalog);
        return "/userspace/catalogedit";
    }




}
