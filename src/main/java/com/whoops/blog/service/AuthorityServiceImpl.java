package com.whoops.blog.service;

import com.whoops.blog.pojo.Authority;
import com.whoops.blog.repository.AuthorityReposity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityReposity authorityReposity;

    /**
     * 根据Id来查询Authority
     *
     * @param id
     */
    @Override
    public Authority getAuthById(Long id) {
        return authorityReposity.getOne(id);
    }
}
