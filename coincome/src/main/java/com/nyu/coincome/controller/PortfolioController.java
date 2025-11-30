package com.nyu.coincome.controller;

import com.nyu.coincome.entity.Result;
import com.nyu.coincome.entity.Users;
import com.nyu.coincome.entity.dto.UserPortfolioDTO;
import com.nyu.coincome.service.PortfolioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    /**
     * 当前用户的推荐组合（含币种权重）
     * GET /api/portfolio/recommended
     */
    @GetMapping("/recommended")
    public Result getRecommendedPortfolios(HttpServletRequest request) {
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("Unauthorized");
        }

        Integer userId = currentUser.getUserId();
        List<UserPortfolioDTO> list = portfolioService.getUserRecommendedPortfolios(userId);

        return Result.success(list);
    }
}
