// src/main/java/com/nyu/coincome/mapper/TxMapper.java
package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.dto.TxDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TxMapper extends BaseMapper<TxDTO> {

    @Select("""
        SELECT
            t.TxID            AS txId,
            t.UserID          AS userId,
            t.ExchangeID      AS exchangeId,
            e.ExchangeName    AS exchangeName,
            t.CoinID          AS coinId,
            c.CoinName        AS coinSymbol,
            t.TxType          AS txType,
            t.Quantity        AS quantity,
            t.Price           AS price,
            t.TxTime          AS txTime
        FROM TransactionRecord t
        LEFT JOIN `Exchange` e ON t.ExchangeID = e.ExchangeID
        LEFT JOIN `Coin` c ON t.CoinID = c.CoinID
        WHERE t.UserID = #{userId}
        ORDER BY t.TxTime DESC, t.TxID DESC
        """
    )
    List<TxDTO> findByUserId(@Param("userId") Integer userId);
}
