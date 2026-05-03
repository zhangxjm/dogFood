package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.entity.Message;
import com.campustrade.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public Result<Message> sendMessage(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        Long receiverId = params.get("receiverId") != null ? 
                Long.parseLong(params.get("receiverId").toString()) : null;
        Long productId = params.get("productId") != null ? 
                Long.parseLong(params.get("productId").toString()) : null;
        String content = params.get("content") != null ? params.get("content").toString() : null;
        
        if (receiverId == null) {
            return Result.error("接收者ID不能为空");
        }
        if (!StringUtils.hasText(content)) {
            return Result.error("消息内容不能为空");
        }
        
        boolean success = messageService.sendMessage(userId, receiverId, productId, content);
        if (success) {
            return Result.success("发送成功");
        }
        return Result.error("发送失败");
    }

    @GetMapping("/received")
    public Result<List<Message>> getReceivedMessages(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        List<Message> messages = messageService.getReceivedMessages(userId);
        return Result.success(messages);
    }

    @GetMapping("/sent")
    public Result<List<Message>> getSentMessages(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        List<Message> messages = messageService.getSentMessages(userId);
        return Result.success(messages);
    }

    @GetMapping("/chat/{otherUserId}")
    public Result<List<Message>> getChatHistory(HttpServletRequest request,
                                                  @PathVariable Long otherUserId,
                                                  @RequestParam(required = false) Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        List<Message> messages = messageService.getChatHistory(userId, otherUserId, productId);
        return Result.success(messages);
    }

    @PostMapping("/read/{id}")
    public Result<Void> markAsRead(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        boolean success = messageService.markAsRead(id);
        if (success) {
            return Result.success("已标记为已读");
        }
        return Result.error("操作失败");
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            Map<String, Integer> result = new HashMap<>();
            result.put("count", 0);
            return Result.success(result);
        }
        
        int count = messageService.getUnreadCount(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }
}
