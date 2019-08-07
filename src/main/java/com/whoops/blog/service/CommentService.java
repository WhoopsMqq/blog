package com.whoops.blog.service;

import com.whoops.blog.pojo.Comment;

public interface CommentService {

    /**
     * 根据id来获取comment
     */
    Comment getCommentById(Long id);

    /**
     * 根据id来移除comment
     */
    void removeComment(Long id);
}
