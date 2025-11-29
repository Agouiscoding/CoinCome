package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AssetMapper extends BaseMapper<Asset> {
    //根据userId
    @Select("SELECT * FROM Asset WHERE UserId=#{userId}")
    List<Asset> findAllAsset(Integer userId);
}
