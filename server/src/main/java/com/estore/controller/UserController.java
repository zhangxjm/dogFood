package com.estore.controller;

import com.estore.common.Result;
import com.estore.entity.User;
import com.estore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/info")
    public Result<User> getInfo(@RequestAttribute("userId") Long userId) {
        try {
            User user = userService.getById(userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/info")
    public Result<User> update(@RequestAttribute("userId") Long userId, @RequestBody User user) {
        try {
            User updated = userService.update(userId, user);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
