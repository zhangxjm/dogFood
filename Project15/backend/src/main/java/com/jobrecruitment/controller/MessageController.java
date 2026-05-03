package com.jobrecruitment.controller;

import com.jobrecruitment.common.Result;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(PageDTO dto) {
        var messages = messageService.listReceived(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", messages);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @GetMapping("/unread/count")
    public Result<Long> unreadCount() {
        Long count = messageService.countUnread();
        return Result.success(count);
    }

    @PutMapping("/read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return Result.success();
    }

    @PutMapping("/read/all")
    public Result<Void> markAllAsRead() {
        messageService.markAllAsRead();
        return Result.success();
    }

    @PostMapping("/send/{receiverId}")
    public Result<Void> send(@PathVariable Long receiverId, @RequestBody Map<String, String> body) {
        messageService.sendMessage(receiverId, body.get("content"));
        return Result.success();
    }

    @GetMapping("/chat/{otherUserId}")
    public Result<?> chat(@PathVariable Long otherUserId, PageDTO dto) {
        var messages = messageService.listChat(otherUserId, dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", messages);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }
}
