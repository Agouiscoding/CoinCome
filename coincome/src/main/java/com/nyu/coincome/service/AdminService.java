package com.nyu.coincome.service;

public interface AdminService {

    /**
     * 判断某个用户是否为管理员
     */
    boolean isAdmin(Integer userId);
}
