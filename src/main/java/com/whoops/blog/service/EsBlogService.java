package com.whoops.blog.service;

import com.whoops.blog.pojo.User;
import com.whoops.blog.pojo.es.EsBlog;
import com.whoops.blog.vo.TagVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EsBlogService {
    /**
     * 移除EsBlog
     */
    void removeEsBlog(String id);

    /**
     * 跟新EsBlog
     */
    EsBlog updateEsBlog(EsBlog esBlog);

    /**
     * 根据blogId获取EsBlog
     */
    EsBlog getEsBlogByBlogId(Long blodId);

    /**
     * 获取最新的EsBlog,即根据时间对博客进行排序
     */
    Page<EsBlog> listNewestEsBlogs(String kewword, Pageable pageable);

    /**
     * 获取最热的EsBlog
     */
    Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable);

    /**
     * 获取所有EsBlo
     */
    Page<EsBlog> listEsBlogs(Pageable pageable);

    /**
     * 获取最新的五个EsBlog
     */
    List<EsBlog> listTop5NewestEsBlogs();

    /**
     * 获取最热的五个EsBlog
     */
    List<EsBlog> listTop5HotestEsBlogs();

    /**
     * 列出标签用的最多的前30个
     */
    List<TagVo> listTop30Tags();

    /**
     * 列出前12的用户
     */
    List<User> listTop12User();


}






























