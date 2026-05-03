package com.logistics.common.feign;

import com.logistics.common.entity.User;
import com.logistics.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", path = "/api/user")
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<User> getById(@PathVariable("id") Long id);

    @GetMapping("/username/{username}")
    Result<User> getByUsername(@PathVariable("username") String username);
}
