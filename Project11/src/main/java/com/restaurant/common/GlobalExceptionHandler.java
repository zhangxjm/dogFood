package com.restaurant.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        log.error("系统异常", e);
        model.addAttribute("error", "系统异常：" + e.getMessage());
        return "error/500";
    }
    
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, Model model) {
        log.error("运行时异常", e);
        model.addAttribute("error", e.getMessage());
        return "error/500";
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException e, Model model) {
        log.warn("权限不足", e);
        model.addAttribute("error", "权限不足，无法访问该页面");
        return "error/403";
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthenticationException(AuthenticationException e, RedirectAttributes redirectAttributes) {
        log.warn("认证失败", e);
        String message = "登录失败";
        if (e instanceof BadCredentialsException) {
            message = "用户名或密码错误";
        } else if (e instanceof DisabledException) {
            message = "用户已被禁用";
        } else if (e instanceof LockedException) {
            message = "用户已被锁定";
        }
        redirectAttributes.addFlashAttribute("error", message);
        return "redirect:/login";
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数异常", e);
        return Result.error(e.getMessage());
    }
}
