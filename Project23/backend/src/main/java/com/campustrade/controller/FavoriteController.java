package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.entity.Favorite;
import com.campustrade.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/add/{productId}")
    public Result<Void> addFavorite(HttpServletRequest request, @PathVariable Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        boolean success = favoriteService.addFavorite(userId, productId);
        if (success) {
            return Result.success("收藏成功");
        }
        return Result.error("收藏失败");
    }

    @PostMapping("/remove/{productId}")
    public Result<Void> removeFavorite(HttpServletRequest request, @PathVariable Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        boolean success = favoriteService.removeFavorite(userId, productId);
        if (success) {
            return Result.success("取消收藏成功");
        }
        return Result.error("取消收藏失败");
    }

    @GetMapping("/my")
    public Result<List<Favorite>> getMyFavorites(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        List<Favorite> favorites = favoriteService.getMyFavorites(userId);
        return Result.success(favorites);
    }

    @GetMapping("/check/{productId}")
    public Result<Boolean> checkFavorite(HttpServletRequest request, @PathVariable Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.success(false);
        }
        
        boolean isFavorited = favoriteService.checkFavorite(userId, productId);
        return Result.success(isFavorited);
    }
}
