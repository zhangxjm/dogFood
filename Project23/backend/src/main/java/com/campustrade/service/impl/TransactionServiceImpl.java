package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campustrade.entity.Product;
import com.campustrade.entity.Transaction;
import com.campustrade.entity.User;
import com.campustrade.mapper.TransactionMapper;
import com.campustrade.service.ProductService;
import com.campustrade.service.TransactionService;
import com.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    private final ProductService productService;
    private final UserService userService;

    @Override
    public Transaction createTransaction(Long buyerId, Long productId, Long sellerId) {
        if (buyerId == null || productId == null) {
            return null;
        }

        Product product = productService.getById(productId);
        if (product == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setProductId(productId);
        transaction.setBuyerId(buyerId);
        transaction.setSellerId(sellerId != null ? sellerId : product.getUserId());
        transaction.setPrice(product.getPrice());
        transaction.setStatus("pending");

        boolean success = this.save(transaction);
        if (success) {
            return transaction;
        }
        return null;
    }

    @Override
    public List<Transaction> getBuyerTransactions(Long buyerId) {
        if (buyerId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getBuyerId, buyerId)
                .orderByDesc(Transaction::getCreateTime);

        List<Transaction> transactions = this.list(wrapper);
        populateTransactionInfo(transactions);
        return transactions;
    }

    @Override
    public List<Transaction> getSellerTransactions(Long sellerId) {
        if (sellerId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getSellerId, sellerId)
                .orderByDesc(Transaction::getCreateTime);

        List<Transaction> transactions = this.list(wrapper);
        populateTransactionInfo(transactions);
        return transactions;
    }

    @Override
    public boolean updateTransactionStatus(Long transactionId, String status) {
        if (transactionId == null || status == null) {
            return false;
        }

        Transaction transaction = this.getById(transactionId);
        if (transaction == null) {
            return false;
        }

        transaction.setStatus(status);
        return this.updateById(transaction);
    }

    private void populateTransactionInfo(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            if (transaction.getProductId() != null) {
                Product product = productService.getById(transaction.getProductId());
                transaction.setProduct(product);
            }
            
            if (transaction.getSellerId() != null) {
                User seller = userService.getById(transaction.getSellerId());
                if (seller != null) {
                    transaction.setSellerNickname(seller.getNickname());
                    transaction.setSellerAvatar(seller.getAvatar());
                }
            }
            
            if (transaction.getBuyerId() != null) {
                User buyer = userService.getById(transaction.getBuyerId());
                if (buyer != null) {
                    transaction.setBuyerNickname(buyer.getNickname());
                    transaction.setBuyerAvatar(buyer.getAvatar());
                }
            }
        }
    }
}
