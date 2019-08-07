package com.whoops.blog.controller;

import com.whoops.blog.pojo.Blog;
import com.whoops.blog.pojo.Comment;
import com.whoops.blog.pojo.User;
import com.whoops.blog.service.BlogServiceImpl;
import com.whoops.blog.service.CommentServiceImpl;
import com.whoops.blog.util.ConstraintViolationExceptionHandler;
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
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private BlogServiceImpl blogService;

    @Autowired
    private CommentServiceImpl commentService;

    /**
     * 获取评论列表
     */
    @GetMapping
    public String listComments(@RequestParam(value = "blogId",required = true) Long blogId, Model model){
        Blog blog = blogService.getBlogById(blogId);
        List<Comment> comments = blog.getCommentList();

        String commentOwner = "";
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null) {
                commentOwner = principal.getUsername();
            }
        }

        model.addAttribute("commentOwner", commentOwner);
        model.addAttribute("comments", comments);
        return "/userspace/blog :: #mainContainerRepleace";
    }


    /**
     * 发表评论
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")//判断用户是否有对于权限
    public ResponseEntity<Response> saveComment(@RequestParam(value = "blogId")Long blogId,@RequestParam(value = "commentContent") String commentContent){
        try {
            blogService.createComment(blogId,commentContent);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        }catch (Exception e){
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true,"发表成功!",null));
    }


    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Response> delComment(@PathVariable("commentId")Long commentId,@RequestParam("blogId")Long blogId){
        boolean commentOwner = false;
        User user = commentService.getCommentById(commentId).getUser();

        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null && user.getUsername().equals(principal.getUsername())) {
                commentOwner = true;
            }
        }

        if (!commentOwner) {
            return ResponseEntity.ok().body(new Response(false, "无法删除他人的评论!"));
        }

        try{
            blogService.removeComment(blogId,commentId);
            commentService.removeComment(commentId);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        }catch (Exception e){
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true,"删除成功!",null));
    }

}
