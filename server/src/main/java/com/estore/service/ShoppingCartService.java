package com.estore.service;

import com.estore.entity.Product;
import com.estore.entity.ShoppingCart;
import com.estore.repository.ProductRepository;
import com.estore.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartService {
    
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    
    public List<ShoppingCart> getByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId);
    }
    
    @Transactional
    public ShoppingCart add(Long userId, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        if (product.getStatus() == 0 || product.getIsDeleted() == 1) {
            throw new RuntimeException("商品已下架");
        }
        
        final Integer finalQuantity = (quantity == null || quantity < 1) ? 1 : quantity;
        
        return shoppingCartRepository.findByUserIdAndProductId(userId, productId)
                .map(cart -> {
                    cart.setQuantity(cart.getQuantity() + finalQuantity);
                    return shoppingCartRepository.save(cart);
                })
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart();
                    cart.setUserId(userId);
                    cart.setProductId(productId);
                    cart.setQuantity(finalQuantity);
                    cart.setSelected(1);
                    return shoppingCartRepository.save(cart);
                });
    }
    
    @Transactional
    public ShoppingCart update(Long userId, Long cartId, Integer quantity, Integer selected) {
        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));
        
        if (!cart.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        
        if (quantity != null) {
            if (quantity < 1) {
                throw new RuntimeException("数量不能小于1");
            }
            cart.setQuantity(quantity);
        }
        
        if (selected != null) {
            cart.setSelected(selected);
        }
        
        return shoppingCartRepository.save(cart);
    }
    
    @Transactional
    public void delete(Long userId, Long cartId) {
        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));
        
        if (!cart.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        
        shoppingCartRepository.delete(cart);
    }
    
    @Transactional
    public void clear(Long userId) {
        shoppingCartRepository.deleteByUserId(userId);
    }
}
