package com.campustrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campustrade.entity.Favorite;

import java.util.List;

public interface FavoriteService extends IService<Favorite> {
    boolean addFavorite(Long userId, Long productId);
    boolean removeFavorite(Long userId, Long productId);
    boolean checkFavorite(Long userId, Long productId);
    List<Favorite> getMyFavorites(Long userId);
}
