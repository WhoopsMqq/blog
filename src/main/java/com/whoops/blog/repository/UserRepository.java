package com.whoops.blog.repository;

import com.whoops.blog.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * 根据名称分页模糊查询用户列表
     * @param name
     * @param pageable
     * @return
     */
    Page<User> findByNameLike(String name, Pageable pageable);

    /**
     * 根据账户名称查询用户
     * @param username
     * @return
     */
    User findUserByUsername(String username);

    /**
     * 根据用户名列表找出用户
     */
    List<User> findByUsernameIn(List<String> usernameList);
}
