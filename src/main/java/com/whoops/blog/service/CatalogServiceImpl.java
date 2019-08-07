package com.whoops.blog.service;

import com.whoops.blog.pojo.Catalog;
import com.whoops.blog.pojo.User;
import com.whoops.blog.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private CatalogRepository catalogRepository;

    /**
     * 保存Catalog
     *
     * @param catalog
     */
    @Override
    @Transactional
    public void saveCatalog(Catalog catalog) {
        //判断是否重复
        List<Catalog> catalogList = catalogRepository.findByUserAndName(catalog.getUser(),catalog.getName());
        if(catalogList != null && catalogList.size() > 0){
            throw new IllegalArgumentException("该分类已经存在!");
        }
        catalogRepository.save(catalog);
    }

    /**
     * 删除Catalog
     *
     * @param catalogId
     */
    @Override
    @Transactional
    public void removeCatalog(Long catalogId) {
        catalogRepository.deleteById(catalogId);
    }

    /**
     * 根据id来查找Catalog
     *
     * @param catalogId
     */
    @Override
    public Catalog getCatalogById(Long catalogId) {
        return catalogRepository.getOne(catalogId);
    }

    /**
     * 根据用户来查找Catalog
     *
     * @param user
     */
    @Override
    public List<Catalog> getCatalogsByUser(User user) {
        return catalogRepository.findByUser(user);
    }
}
