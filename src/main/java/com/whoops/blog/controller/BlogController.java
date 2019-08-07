package com.whoops.blog.controller;

import com.whoops.blog.pojo.User;
import com.whoops.blog.pojo.es.EsBlog;
import com.whoops.blog.service.EsBlogServiceImpl;
import com.whoops.blog.vo.TagVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * blog控制器
 */
@Controller
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private EsBlogServiceImpl esBlogService;

    /**
     * 根据条件列出博客文章
     * order:排序规则
     * keyword:关键字
     */
    @GetMapping
    public String listBlogs(@RequestParam(value = "order",required = false,defaultValue = "new") String order,
                            @RequestParam(value = "keyword",required = false,defaultValue = "") String keyword,
                            @RequestParam(value = "async",required = false) boolean async,
                            @RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex,
                            @RequestParam(value = "pageSize" ,required = false,defaultValue = "10")int pageSize,
                            Model model){
        Page<EsBlog> page = null;
        List<EsBlog> esBlogList = null;
        boolean isEmpty = true;//系统初始化时没有博客
        try{
            if(StringUtils.equals(order,"hot")){//最热查询
                Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","voteSize","createTime");
                Pageable pageable = new PageRequest(pageIndex,pageSize,sort);
                page = esBlogService.listHotestEsBlogs(keyword,pageable);
            }else if(StringUtils.equals(order,"new")){//查询最新
                Sort sort = new Sort(Sort.Direction.DESC,"createTime");
                Pageable pageable = new PageRequest(pageIndex,pageSize,sort);
                page = esBlogService.listNewestEsBlogs(keyword,pageable);
            }
        }catch (Exception e){
            Pageable pageable = new PageRequest(pageIndex,pageSize);
            page = esBlogService.listEsBlogs(pageable);
        }
        esBlogList = page.getContent();
        if(esBlogList.size() > 0){
            isEmpty = false;
        }
        model.addAttribute("order",order);
        model.addAttribute("keyword",keyword);
        model.addAttribute("page",page);
        model.addAttribute("blogList",esBlogList);

        if(!async && !isEmpty){//首次访问才加载
            List<EsBlog> newest = esBlogService.listTop5NewestEsBlogs();
            model.addAttribute("newest",newest);
            List<EsBlog> hotest = esBlogService.listTop5HotestEsBlogs();
            model.addAttribute("hotest",hotest);
            /*List<TagVo> tags = esBlogService.listTop30Tags();
            model.addAttribute("tags",tags);*/
            List<User> users = esBlogService.listTop12User();
            model.addAttribute("users",users);
        }

        return (async == true ? "/index :: #mainContainerRepleace" : "/index");
    }


}









































