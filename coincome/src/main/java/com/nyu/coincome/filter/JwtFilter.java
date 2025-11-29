package com.nyu.coincome.filter;

import com.nyu.coincome.entity.Users;
import com.nyu.coincome.mapper.UsersMapper;
import com.nyu.coincome.utils.JwtUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String url = req.getRequestURI();

        // 不需要 Token 的接口放行
        if (url.contains("/signin") ||
                url.contains("/google") ||
                url.contains("/check-username") ||
                url.contains("/register"))
        {
            chain.doFilter(request, response);
            return;
        }

        // 获取前端 Authorization
        String auth = req.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        try {
            String token = auth.substring(7);
            String userId = jwtUtil.getUserId(token);

            Users user = usersMapper.selectById(userId);
            if (user == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Invalid user in token");
                return;
            }

            req.setAttribute("currentUser", user);

            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("e: ", e);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Invalid or expired token");
        }
    }
}
