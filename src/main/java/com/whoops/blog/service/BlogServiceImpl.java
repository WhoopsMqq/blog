package com.whoops.blog.service;

import com.whoops.blog.pojo.*;
import com.whoops.blog.pojo.es.EsBlog;
import com.whoops.blog.repository.BlogRepository;
import com.whoops.blog.repository.CatalogRepository;
import com.whoops.blog.repository.es.EsBlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private EsBlogServiceImpl esBlogService;

    /**
     * 保存博客
     *
     * @param blog
     */
    @Override
    @Transactional
    public Blog saveBlog(Blog blog) {
        boolean isNew = false;
        if(blog.getId() == 0 || blog.getId() == null){
            isNew = true;
        }
        EsBlog esBlog = null;
        Blog returnBlog = blogRepository.save(blog);
        if(isNew){
            esBlog = new EsBlog(returnBlog);
        }else{
            esBlog = esBlogService.getEsBlogByBlogId(returnBlog.getId());
            esBlog.update(returnBlog);
        }
        esBlogService.updateEsBlog(esBlog);
        return returnBlog;
    }

    /**
     * 删除博客
     *
     * @param id
     */
    @Override
    @Transactional
    public void delBlog(Long id) {
        EsBlog esBlog = esBlogService.getEsBlogByBlogId(id);
        esBlogService.removeEsBlog(esBlog.getId());
        blogRepository.deleteById(id);
    }

    /**
     * 根据id获取博客
     *
     * @param id
     */
    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.getOne(id);
    }

    /**
     * 跟新博客
     *
     * @param blog
     */
    @Override
    @Transactional
    public Blog updateBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    /**
     * 根据用户进行博客名模糊查询(最新)
     *
     * @param user
     * @param title
     * @param pageable
     */
    @Override
    public Page<Blog> listBlogByUserAndTitleLikeVote(User user, String title, Pageable pageable) {
        title = "%" + title + "%";
        return blogRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user,title,pageable);
    }

    /**
     * 根据用户进行博客名模糊查询(最热)
     *
     * @param user
     * @param title
     * @param pageable
     */
    @Override
    public Page<Blog> listBlogByUserAndTitleLikeVoteAndSort(User user, String title, Pageable pageable) {
        title = "%" + title + "%";
        return blogRepository.findByUserAndTitleLike(user, title, pageable);
    }

    /**
     * 根据Catalog获取blog
     *
     * @param catalog
     * @param pageable
     */
    @Override
    public Page<Blog> findByCatalog(Catalog catalog, Pageable pageable) {
        return blogRepository.findByCatalog(catalog,pageable);
    }

    /**
     * 根据Catalog获取blog
     *
     * @param catalog
     */
    public List<Blog> findByCatalog(Catalog catalog) {
        return blogRepository.findByCatalog(catalog);
    }



    /**
     * 阅读量递增
     *
     * @param id
     */
    @Override
    @Transactional
    public void readingIncrease(Long id) {
        Blog blog = blogRepository.getOne(id);
        blog.setReadSize(blog.getReadSize()+1);
        this.saveBlog(blog);
    }

    /**
     * 创建发表评论
     *
     * @param blogId
     * @param commentContent
     */
    @Override
    @Transactional
    public Blog createComment(Long blogId, String commentContent) {
        Blog blog = blogRepository.getOne(blogId);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = new Comment(commentContent,user);
        blog.addComment(comment);
        return this.saveBlog(blog);
    }

    /**
     * 删除评论
     *
     * @param blogId
     * @param commentId
     */
    @Override
    @Transactional
    public void removeComment(Long blogId, Long commentId) {
        Blog blog = blogRepository.getOne(blogId);
        blog.delComment(commentId);
        this.saveBlog(blog);
    }

    /**
     * 点赞
     *
     * @param blogId
     */
    @Override
    @Transactional
    public Blog saveVote(Long blogId) {
        Blog blog = blogRepository.getOne(blogId);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Vote vote = new Vote(user);
        boolean isExist = blog.addVote(vote);
        if(!isExist){
            throw new IllegalArgumentException("该用户已点赞!");
        }

        return blogRepository.save(blog);
    }

    /**
     * 取消点赞
     *
     * @param blogId
     * @param voteId
     */
    @Override
    @Transactional
    public void removeVote(Long blogId, Long voteId) {
        Blog blog = blogRepository.getOne(blogId);
        blog.removeVote(voteId);
        blogRepository.save(blog);
    }
}
