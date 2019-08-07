package com.whoops.blog.repository.es;

import com.whoops.blog.pojo.es.EsBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsBlogRepository extends ElasticsearchRepository<EsBlog,String> {

    Page<EsBlog> findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(String title, String summary, String content, String tags, Pageable pageable);

    EsBlog findByBlogId(Long blogId);
}
