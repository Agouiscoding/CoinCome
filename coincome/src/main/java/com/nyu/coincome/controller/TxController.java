package com.nyu.coincome.controller;

import com.nyu.coincome.entity.Result;
import com.nyu.coincome.entity.Users;
import com.nyu.coincome.entity.dto.TxDTO;
import com.nyu.coincome.service.TxService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tx")
public class TxController {

    private final TxService txService;

    public TxController(TxService txService) {
        this.txService = txService;
    }

    @GetMapping("/transactions")
    public Result listUserTx(HttpServletRequest request) {
        Users currentUser = (Users) request.getAttribute("currentUser");
        System.out.println("[TradeController] currentUser = " + currentUser);
        if (currentUser == null) {
            return Result.error("Invalid token");
        }
        Integer userId = currentUser.getUserId();

        List<TxDTO> list = txService.listUserTx(userId);
        return Result.success(list);
    }
}
