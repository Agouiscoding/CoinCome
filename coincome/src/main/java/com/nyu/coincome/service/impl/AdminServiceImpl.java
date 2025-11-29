package com.nyu.coincome.service.impl;

import com.nyu.coincome.mapper.AdminMapper;
import com.nyu.coincome.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private final AdminMapper adminMapper;

    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public boolean isAdmin(Integer userId) {
        if (userId == null) return false;
        return adminMapper.isAdmin(userId);
    }
}
