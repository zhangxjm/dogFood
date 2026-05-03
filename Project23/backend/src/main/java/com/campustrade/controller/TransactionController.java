package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.entity.Product;
import com.campustrade.entity.Transaction;
import com.campustrade.service.ProductService;
import com.campustrade.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final ProductService productService;

    @PostMapping("/create")
    public Result<Transaction> createTransaction(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        Long productId = params.get("productId") != null ? 
                Long.parseLong(params.get("productId").toString()) : null;
        
        if (productId == null) {
            return Result.error("商品ID不能为空");
        }
        
        Product product = productService.getById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        Transaction transaction = transactionService.createTransaction(userId, productId, product.getUserId());
        if (transaction != null) {
            return Result.success("交易创建成功", transaction);
        }
        return Result.error("创建失败");
    }

    @GetMapping("/buyer")
    public Result<List<Transaction>> getBuyerTransactions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        List<Transaction> transactions = transactionService.getBuyerTransactions(userId);
        return Result.success(transactions);
    }

    @GetMapping("/seller")
    public Result<List<Transaction>> getSellerTransactions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        List<Transaction> transactions = transactionService.getSellerTransactions(userId);
        return Result.success(transactions);
    }

    @PostMapping("/update-status/{id}")
    public Result<Void> updateTransactionStatus(HttpServletRequest request, 
                                                  @PathVariable Long id,
                                                  @RequestBody Map<String, String> params) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        String status = params.get("status");
        if (!StringUtils.hasText(status)) {
            return Result.error("状态不能为空");
        }
        
        boolean success = transactionService.updateTransactionStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("更新失败");
    }
}
