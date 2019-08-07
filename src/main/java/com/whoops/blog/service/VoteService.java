package com.whoops.blog.service;

import com.whoops.blog.pojo.Vote;

public interface VoteService  {
    /**
     * 根据id获取Vote
     */
    Vote getVoteById(Long voteId);

    /**
     * 取消点赞
     */
    void removeVote(Long voteId);
}
