package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campustrade.entity.Favorite;
import com.campustrade.entity.Product;
import com.campustrade.mapper.FavoriteMapper;
import com.campustrade.service.FavoriteService;
import com.campustrade.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final ProductService productService;

    @Override
    public boolean addFavorite(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return false;
        }

        if (checkFavorite(userId, productId)) {
            return true;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        
        boolean success = this.save(favorite);
        
        if (success) {
            Product product = productService.getById(productId);
            if (product != null) {
                product.setFavoriteCount(product.getFavoriteCount() == null ? 1 : product.getFavoriteCount() + 1);
                productService.updateById(product);
            }
        }
        
        return success;
    }

    @Override
    public boolean removeFavorite(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return false;
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        
        boolean success = this.remove(wrapper);
        
        if (success) {
            Product product = productService.getById(productId);
            if (product != null && product.getFavoriteCount() != null && product.getFavoriteCount() > 0) {
                product.setFavoriteCount(product.getFavoriteCount() - 1);
                productService.updateById(product);
            }
        }
        
        return success;
    }

    @Override
    public boolean checkFavorite(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return false;
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        
        return this.count(wrapper) > 0;
    }

    @Override
    public List<Favorite> getMyFavorites(Long userId) {
        if (userId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);
        
        List<Favorite> favorites = this.list(wrapper);
        
        for (Favorite favorite : favorites) {
            if (favorite.getProductId() != null) {
                Product product = productService.getById(favorite.getProductId());
                favorite.setProduct(product);
            }
        }
        
        return favorites;
    }
}
