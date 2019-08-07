package com.whoops.blog.service;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import com.whoops.blog.pojo.User;
import com.whoops.blog.pojo.es.EsBlog;
import com.whoops.blog.repository.es.EsBlogRepository;
import com.whoops.blog.vo.TagVo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class EsBlogServiceImpl implements EsBlogService {

    @Autowired
    private EsBlogRepository esBlogRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private static final Pageable TOP_5_PAGEABLE = new PageRequest(0, 5);
    private static final String EMPTY_KEYWORD = "";

    /**
     * 移除EsBlog
     *
     * @param id
     */
    @Override
    public void removeEsBlog(String id) {
        esBlogRepository.deleteById(id);
    }

    /**
     * 跟新EsBlog
     *
     * @param esBlog
     */
    @Override
    public EsBlog updateEsBlog(EsBlog esBlog) {
        return esBlogRepository.save(esBlog);
    }

    /**
     * 根据blogId获取EsBlog
     *
     * @param blodId
     */
    @Override
    public EsBlog getEsBlogByBlogId(Long blodId) {
        return esBlogRepository.findByBlogId(blodId);
    }

    /**
     * 获取最新的EsBlog,即根据时间对博客进行排序
     *
     * @param kewword
     * @param pageable
     */
    @Override
    public Page<EsBlog> listNewestEsBlogs(String kewword, Pageable pageable) {
        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        if(pageable.getSort() == null){
            pageable = new PageRequest(pageable.getPageNumber(),pageable.getPageSize(),sort);
        }
        Page<EsBlog> page = esBlogRepository.findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(kewword,kewword,kewword,kewword,pageable);
        return page;
    }

    /**
     * 获取最热的EsBlog
     *
     * @param keyword
     * @param pageable
     */
    @Override
    public Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable) {
        Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","voteSize","createTime");
        if(pageable.getSort() == null){
            pageable = new PageRequest(pageable.getPageNumber(),pageable.getPageSize(),sort);
        }
        return esBlogRepository.findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(keyword,keyword,keyword,keyword,pageable);
    }

    /**
     * 获取所有EsBlo
     *
     * @param pageable
     */
    @Override
    public Page<EsBlog> listEsBlogs(Pageable pageable) {
        return esBlogRepository.findAll(pageable);
    }

    /**
     * 获取最新的五个EsBlog
     */
    @Override
    public List<EsBlog> listTop5NewestEsBlogs() {
        return listNewestEsBlogs(EMPTY_KEYWORD,TOP_5_PAGEABLE).getContent();
    }

    /**
     * 获取最热的五个EsBlog
     */
    @Override
    public List<EsBlog> listTop5HotestEsBlogs() {
        return listHotestEsBlogs(EMPTY_KEYWORD,TOP_5_PAGEABLE).getContent();
    }

    /**
     * 列出标签用的最多的前30个
     */
    @Override
    public List<TagVo> listTop30Tags() {
        List<TagVo> list = new ArrayList<>();
        // given
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH)
                .withIndices("blogs").withTypes("blogs")
                .addAggregation(terms("tags").field("tags").order(Terms.Order.count(false)).size(30))
                .build();
        // when
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        StringTerms modelTerms =  (StringTerms)aggregations.asMap().get("tags");

        Iterator<StringTerms.Bucket> modelBucketIt = modelTerms.getBuckets().iterator();

        while (modelBucketIt.hasNext()) {
            Bucket actiontypeBucket = modelBucketIt.next();

            list.add(new TagVo(actiontypeBucket.getKey().toString(),
                    actiontypeBucket.getDocCount()));
        }
        return list;
    }

    /**
     * 列出前12的用户
     */
    @Override
    public List<User> listTop12User() {
        List<String> usernameList = new ArrayList<>();
        //given
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH)
                .withIndices("blogs").withTypes("blogs")
                .addAggregation(terms("users").field("username").order(Terms.Order.count(false)).size(12)).build();

        //when
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        StringTerms modelTerms = (StringTerms)aggregations.asMap().get("users");

        Iterator<StringTerms.Bucket> modelBucketIt = modelTerms.getBuckets().iterator();
        while (modelBucketIt.hasNext()) {
            Bucket actiontypeBucket = modelBucketIt.next();
            String username = actiontypeBucket.getKey().toString();
            usernameList.add(username);
        }

        List<User> userList = userService.listUsersByUsernames(usernameList);

        return userList;
    }
}








