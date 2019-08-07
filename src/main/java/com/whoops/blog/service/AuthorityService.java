package com.whoops.blog.service;

import com.whoops.blog.pojo.Authority;

public interface AuthorityService {
    /**
     * 根据Id来查询Authority
     */
    Authority getAuthById(Long id);
}
