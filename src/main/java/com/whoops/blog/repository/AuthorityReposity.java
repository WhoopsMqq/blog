package com.whoops.blog.repository;

import com.whoops.blog.pojo.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityReposity extends JpaRepository<Authority, Long> {
}
