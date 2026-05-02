package com.estore.controller;

import com.estore.common.Result;
import com.estore.entity.ShoppingCart;
import com.estore.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    
    private final ShoppingCartService shoppingCartService;
    
    @GetMapping
    public Result<List<ShoppingCart>> list(@RequestAttribute("userId") Long userId) {
        try {
            List<ShoppingCart> carts = shoppingCartService.getByUserId(userId);
            return Result.success(carts);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping
    public Result<ShoppingCart> add(
            @RequestAttribute("userId") Long userId,
            @RequestBody Map<String, Object> params) {
        try {
            Long productId = Long.valueOf(params.get("productId").toString());
            Integer quantity = params.get("quantity") != null ? 
                    Integer.valueOf(params.get("quantity").toString()) : 1;
            
            ShoppingCart cart = shoppingCartService.add(userId, productId, quantity);
            return Result.success(cart);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<ShoppingCart> update(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id,
            @RequestBody Map<String, Object> params) {
        try {
            Integer quantity = params.get("quantity") != null ? 
                    Integer.valueOf(params.get("quantity").toString()) : null;
            Integer selected = params.get("selected") != null ? 
                    Integer.valueOf(params.get("selected").toString()) : null;
            
            ShoppingCart cart = shoppingCartService.update(userId, id, quantity, selected);
            return Result.success(cart);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        try {
            shoppingCartService.delete(userId, id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/clear")
    public Result<Void> clear(@RequestAttribute("userId") Long userId) {
        try {
            shoppingCartService.clear(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
