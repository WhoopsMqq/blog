package com.whoops.blog.service;

import com.whoops.blog.pojo.User;
import com.whoops.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService , UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    /**
     * 保存修改编辑用户
     *
     * @param user
     * @return
     */
    @Transactional
    @Override
    public User saveOrUpdateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    @Transactional
    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @Transactional
    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 根据id查找用户
     *
     * @param id
     * @return
     */
    @Override
    public User getUserById(Long id) {
//        Optional<User> userOptional = userRepository.findById(id);
        return userRepository.getOne(id);
    }

    /**
     * 根据用户姓名进行分页模糊查询
     *
     * @param name
     * @param pageable
     * @return
     */
    @Override
    public Page<User> listUsersByNameLike(String name, Pageable pageable) {
        name = "%"+name+"%";
        return userRepository.findByNameLike(name, pageable);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(s);
    }

    public User findUserByUsername(String username){
        return userRepository.findUserByUsername(username);
    }

    /**
     * 根据用户名列表找出用户
     *
     * @param usernameList
     */
    @Override
    public List<User> listUsersByUsernames(List<String> usernameList) {
        return userRepository.findByUsernameIn(usernameList);
    }
}
































