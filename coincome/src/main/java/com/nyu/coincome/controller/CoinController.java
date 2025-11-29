package com.nyu.coincome.controller;

import com.nyu.coincome.entity.Result;
import com.nyu.coincome.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/admin/coin")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    // ✅ 旧的 path 版本（你如果不用可以删掉）
    @PostMapping("/import-json")
    public Result importFromJsonByPath(@RequestParam String path) {
        try {
            int inserted = coinService.importFromJson(path);
            return Result.success("Inserted: " + inserted);
        } catch (Exception e) {
            return Result.error("Import failed: " + e.getMessage());
        }
    }

    // ✅ 新增：前端上传 JSON 文件
    @PostMapping("/import-json-file")
    public Result importFromJsonFile(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            int inserted = coinService.importFromJsonStream(is);
            return Result.success("Inserted: " + inserted);
        } catch (Exception e) {
            return Result.error("Import failed: " + e.getMessage());
        }
    }
}
