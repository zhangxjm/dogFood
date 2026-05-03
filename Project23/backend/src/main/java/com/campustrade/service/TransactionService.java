package com.campustrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campustrade.entity.Transaction;

import java.util.List;

public interface TransactionService extends IService<Transaction> {
    Transaction createTransaction(Long buyerId, Long productId, Long sellerId);
    List<Transaction> getBuyerTransactions(Long buyerId);
    List<Transaction> getSellerTransactions(Long sellerId);
    boolean updateTransactionStatus(Long transactionId, String status);
}
