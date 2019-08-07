package com.whoops.blog.repository;

import com.whoops.blog.pojo.Catalog;
import com.whoops.blog.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog,Long> {

    /**
     * 根据用户来查询
     */
    List<Catalog> findByUser(User user);

    /**
     * 根据用户,分类名来查询
     */
    List<Catalog> findByUserAndName(User user,String name);

}
