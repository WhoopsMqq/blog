package com.whoops.blog.repository;

import com.whoops.blog.pojo.Blog;
import com.whoops.blog.pojo.Catalog;
import com.whoops.blog.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Long> {
    /**
     * 根据用户名,博客标题分页查询
     */
    Page<Blog> findByUserAndTitleLike(User user, String title, Pageable pageable);

    /**
     * 根据用户名,博客查询(时间逆序)
     */
    Page<Blog> findByUserAndTitleLikeOrderByCreateTimeDesc(User user,String title,Pageable pageable);

    /**
     * 根据分类来查询博客列表
     */
    Page<Blog> findByCatalog(Catalog catalog,Pageable pageable);

    /**
     * 根据分类来查询博客列表
     */
    List<Blog> findByCatalog(Catalog catalog);
}
