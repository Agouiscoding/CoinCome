package com.nyu.coincome.service.impl;

import com.nyu.coincome.entity.Portfolio;
import com.nyu.coincome.entity.dto.PortfolioCoinWeightDTO;
import com.nyu.coincome.entity.dto.UserPortfolioDTO;
import com.nyu.coincome.mapper.PortfolioMapper;
import com.nyu.coincome.mapper.PortfolioWeightMapper;
import com.nyu.coincome.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioMapper portfolioMapper;
    private final PortfolioWeightMapper portfolioWeightMapper;

    @Override
    public List<UserPortfolioDTO> getUserRecommendedPortfolios(Integer userId) {
        // 1. 先查出该用户的所有 Portfolio（一般是 3 个 risk level）
        List<Portfolio> portfolios = portfolioMapper.findByUserId(userId);

        List<UserPortfolioDTO> result = new ArrayList<>();
        for (Portfolio p : portfolios) {
            UserPortfolioDTO dto = new UserPortfolioDTO();
            dto.setPortId(p.getPortId());
            dto.setUserId(p.getUserId());
            dto.setRiskLevel(p.getRiskLevel());
            dto.setOptimizationDate(p.getOptimizationDate());
            dto.setTargetReturn(p.getTargetReturn());
            dto.setTargetRisk(p.getTargetRisk());
            dto.setNote(p.getNote());

            // 2. 查出这个组合下所有币的权重（带 Coin 名字）
            List<PortfolioCoinWeightDTO> weights =
                    portfolioWeightMapper.findWeightsByPortId(p.getPortId());
            dto.setWeights(weights);

            result.add(dto);
        }
        return result;
    }
}
