package com.whoops.blog.service;

import com.whoops.blog.pojo.Blog;
import com.whoops.blog.pojo.Catalog;
import com.whoops.blog.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    /**
     * 保存博客
     */
    Blog saveBlog(Blog blog);

    /**
     * 删除博客
     */
    void delBlog(Long id);

    /**
     * 根据id获取博客
     */
    Blog getBlogById(Long id);

    /**
     * 跟新博客
     */
    Blog updateBlog(Blog blog);

    /**
     * 根据用户进行博客名模糊查询(最新)
     */
    Page<Blog> listBlogByUserAndTitleLikeVote(User user, String title, Pageable pageable);

    /**
     * 根据用户进行博客名模糊查询(最热)
     */
    Page<Blog> listBlogByUserAndTitleLikeVoteAndSort(User user,String title,Pageable pageable);

    /**
     * 根据Catalog获取blog
     */
    Page<Blog> findByCatalog(Catalog catalog,Pageable pageable);

    /**
     * 阅读量递增
     */
    void readingIncrease(Long id);

    /**
     * 创建发表评论
     */
    Blog createComment(Long blogId,String commentContent);

    /**
     * 删除评论
     */
    void removeComment(Long blogId,Long commentId);

    /**
     * 点赞
     */
    Blog saveVote(Long blogId);

    /**
     * 取消点赞
     */
    void removeVote(Long blogId,Long voteId);



}






























