package com.nyu.coincome.service;

import com.nyu.coincome.entity.dto.UserPortfolioDTO;

import java.util.List;

public interface PortfolioService {

    /**
     * 查询某个用户的推荐组合（3 个 risk level），带每个组合里的币权重
     */
    List<UserPortfolioDTO> getUserRecommendedPortfolios(Integer userId);
}
