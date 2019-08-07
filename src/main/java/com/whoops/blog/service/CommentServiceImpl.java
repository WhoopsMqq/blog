package com.whoops.blog.service;

import com.whoops.blog.pojo.Comment;
import com.whoops.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    /**
     * 根据id来获取comment
     *
     * @param id
     */
    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.getOne(id);
    }

    /**
     * 根据id来移除comment
     *
     * @param id
     */
    @Override
    @Transactional
    public void removeComment(Long id) {
        commentRepository.deleteById(id);
    }
}
