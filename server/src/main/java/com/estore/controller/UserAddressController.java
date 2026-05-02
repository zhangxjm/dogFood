package com.estore.controller;

import com.estore.common.Result;
import com.estore.entity.UserAddress;
import com.estore.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/addresses")
@RequiredArgsConstructor
public class UserAddressController {
    
    private final UserAddressService userAddressService;
    
    @GetMapping
    public Result<List<UserAddress>> list(@RequestAttribute("userId") Long userId) {
        try {
            List<UserAddress> addresses = userAddressService.getByUserId(userId);
            return Result.success(addresses);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<UserAddress> getById(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        try {
            UserAddress address = userAddressService.getByIdAndUserId(id, userId);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/default")
    public Result<UserAddress> getDefault(@RequestAttribute("userId") Long userId) {
        try {
            UserAddress address = userAddressService.getDefault(userId);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping
    public Result<UserAddress> create(
            @RequestAttribute("userId") Long userId,
            @RequestBody UserAddress address) {
        try {
            UserAddress created = userAddressService.create(userId, address);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<UserAddress> update(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id,
            @RequestBody UserAddress address) {
        try {
            UserAddress updated = userAddressService.update(userId, id, address);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        try {
            userAddressService.delete(userId, id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/default")
    public Result<UserAddress> setDefault(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        try {
            UserAddress address = userAddressService.setDefault(userId, id);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
