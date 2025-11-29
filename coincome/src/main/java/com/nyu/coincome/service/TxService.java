package com.nyu.coincome.service;

import com.nyu.coincome.entity.dto.TxDTO;

import java.util.List;

public interface TxService {

    /**
     * 获取某个用户的所有交易记录
     */
    List<TxDTO> listUserTx(Integer userId);
}