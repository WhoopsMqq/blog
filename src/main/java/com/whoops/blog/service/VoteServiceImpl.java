package com.whoops.blog.service;

import com.whoops.blog.pojo.Vote;
import com.whoops.blog.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImpl implements VoteService {
    @Autowired
    private VoteRepository voteRepository;

    /**
     * 根据id获取Vote
     *
     * @param voteId
     */
    @Override
    public Vote getVoteById(Long voteId) {
        return voteRepository.getOne(voteId);
    }

    /**
     * 取消点赞
     *
     * @param voteId
     */
    @Override
    public void removeVote(Long voteId) {
        voteRepository.deleteById(voteId);
    }
}
