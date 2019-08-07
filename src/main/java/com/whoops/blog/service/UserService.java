package com.whoops.blog.service;

import com.whoops.blog.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 保存修改编辑用户
     * @param user
     * @return
     */
    User saveOrUpdateUser(User user);

    /**
     * 注册用户
     * @param user
     * @return
     */
    User registerUser(User user);

    /**
     * 删除用户
     * @param id
     */
    void removeUser(Long id);

    /**
     * 根据id查找用户
     * @param id
     * @return
     */
    User getUserById(Long id);

    /**
     * 根据用户姓名进行分页模糊查询
     * @param name
     * @param pageable
     * @return
     */
    Page<User> listUsersByNameLike(String name, Pageable pageable);

    /**
     * 根据用户名列表找出用户
     */
    List<User> listUsersByUsernames(List<String> usernameList);

}
