package com.whoops.blog.controller;

import com.whoops.blog.pojo.Blog;
import com.whoops.blog.pojo.User;
import com.whoops.blog.pojo.Vote;
import com.whoops.blog.service.BlogServiceImpl;
import com.whoops.blog.service.VoteServiceImpl;
import com.whoops.blog.util.ConstraintViolationExceptionHandler;
import com.whoops.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

@Controller
@RequestMapping("/votes")
public class VoteController {

    @Autowired
    private VoteServiceImpl voteService;

    @Autowired
    private BlogServiceImpl blogService;

    /**
     * 发表点赞
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @Transactional
    public ResponseEntity<Response> createVote(@RequestParam("blogId")Long blogId, Model model){
        try{
            blogService.saveVote(blogId);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        }catch (Exception e){
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true,"点赞成功!",null));
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{voteId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Response> delVote(@PathVariable("voteId") Long voteId,@RequestParam("blogId") Long blogId,Model model){
        boolean isVoteOwner = false;
        Vote vote = voteService.getVoteById(voteId);
        User user = vote.getUser();
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null && user.getUsername().equals(principal.getUsername())) {
                isVoteOwner = true;
            }
        }
        if(!isVoteOwner){
            return ResponseEntity.ok().body(new Response(false, "无法取消别人的点赞!"));
        }

        try{
            Blog blog = blogService.getBlogById(blogId);
            blog.removeVote(voteId);
            blogService.saveBlog(blog);
            voteService.removeVote(voteId);
        }catch (ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        }catch (Exception e){
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true,"取消点赞!",null));
    }




}
