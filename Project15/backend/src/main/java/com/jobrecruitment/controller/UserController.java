package com.jobrecruitment.controller;

import com.jobrecruitment.common.Result;
import com.jobrecruitment.dto.LoginDTO;
import com.jobrecruitment.dto.LoginVO;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.dto.RegisterDTO;
import com.jobrecruitment.entity.User;
import com.jobrecruitment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<User> getCurrentUser() {
        User user = userService.getCurrentUser();
        return Result.success(user);
    }

    @GetMapping("/list")
    public Result<?> list(PageDTO dto) {
        var users = userService.list(dto);
        var total = userService.count(dto);
        return Result.success(new Object(){
            public List<?> getRecords() { return users; }
            public Long getTotal() { return total; }
            public Integer getPageNum() { return dto.getPageNum(); }
            public Integer getPageSize() { return dto.getPageSize(); }
        });
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
}
