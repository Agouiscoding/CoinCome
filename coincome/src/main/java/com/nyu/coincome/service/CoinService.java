package com.nyu.coincome.service;

import java.io.InputStream;

public interface CoinService {
    // 旧：从服务器本地路径读（可选）
    int importFromJson(String path) throws Exception;

    // 新：从上传文件流读
    int importFromJsonStream(InputStream is) throws Exception;
}
