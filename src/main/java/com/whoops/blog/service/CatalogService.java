package com.whoops.blog.service;

import com.whoops.blog.pojo.Catalog;
import com.whoops.blog.pojo.User;

import java.util.List;

public interface CatalogService {

    /**
     * 保存Catalog
     */
    void saveCatalog(Catalog catalog);

    /**
     * 删除Catalog
     */
    void removeCatalog(Long catalogId);

    /**
     * 根据id来查找Catalog
     */
    Catalog getCatalogById(Long catalogId);

    /**
     * 根据用户来查找Catalog
     */
    List<Catalog> getCatalogsByUser(User user);


}
