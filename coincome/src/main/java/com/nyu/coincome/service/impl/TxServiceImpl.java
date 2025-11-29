package com.nyu.coincome.service.impl;

import com.nyu.coincome.entity.dto.TxDTO;
import com.nyu.coincome.mapper.TxMapper;
import com.nyu.coincome.service.TxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TxServiceImpl implements TxService {

    private final TxMapper txMapper;

    @Override
    public List<TxDTO> listUserTx(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return txMapper.findByUserId(userId);
    }
}