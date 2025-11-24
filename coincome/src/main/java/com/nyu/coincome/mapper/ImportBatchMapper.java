package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.ImportBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ImportBatchMapper extends BaseMapper<ImportBatch> {
    //查询相同SHA256
    @Select("SELECT COUNT(*) FROM ImportBatch WHERE FileChecksumSHA256 = #{checksum}")
    int countByChecksum(String checksum);

    //批量插入TradeRaw
    void loadTradeRawFromCsv(@Param("filePath") String filePath,
                             @Param("batchId") Integer batchId);

    void resetRowNum();

    // 把 TradeRaw 解析插入 TransactionRecord
    int insertParsedTransactions(@Param("batchId") Integer batchId);

    void callRefreshUserCoinAgg(Integer userId);
}
