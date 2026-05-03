package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campustrade.entity.Message;
import com.campustrade.entity.User;
import com.campustrade.mapper.MessageMapper;
import com.campustrade.service.MessageService;
import com.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final UserService userService;

    @Override
    public boolean sendMessage(Long fromUserId, Long toUserId, Long productId, String content) {
        if (fromUserId == null || toUserId == null || !StringUtils.hasText(content)) {
            return false;
        }

        Message message = new Message();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setProductId(productId);
        message.setContent(content);
        message.setIsRead(0);

        return this.save(message);
    }

    @Override
    public List<Message> getReceivedMessages(Long userId) {
        if (userId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getToUserId, userId)
                .orderByDesc(Message::getCreateTime);

        List<Message> messages = this.list(wrapper);
        populateSenderInfo(messages);
        return messages;
    }

    @Override
    public List<Message> getSentMessages(Long userId) {
        if (userId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getFromUserId, userId)
                .orderByDesc(Message::getCreateTime);

        List<Message> messages = this.list(wrapper);
        populateReceiverInfo(messages);
        return messages;
    }

    @Override
    public List<Message> getChatHistory(Long userId, Long otherUserId, Long productId) {
        if (userId == null || otherUserId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getFromUserId, userId).eq(Message::getToUserId, otherUserId)
                .or().eq(Message::getFromUserId, otherUserId).eq(Message::getToUserId, userId));
        
        if (productId != null) {
            wrapper.eq(Message::getProductId, productId);
        }
        
        wrapper.orderByAsc(Message::getCreateTime);

        List<Message> messages = this.list(wrapper);
        populateSenderInfo(messages);
        return messages;
    }

    @Override
    public boolean markAsRead(Long messageId) {
        if (messageId == null) {
            return false;
        }

        Message message = this.getById(messageId);
        if (message == null) {
            return false;
        }

        message.setIsRead(1);
        return this.updateById(message);
    }

    @Override
    public int getUnreadCount(Long userId) {
        if (userId == null) {
            return 0;
        }

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getToUserId, userId)
                .eq(Message::getIsRead, 0);

        return Math.toIntExact(this.count(wrapper));
    }

    private void populateSenderInfo(List<Message> messages) {
        for (Message message : messages) {
            if (message.getFromUserId() != null) {
                User user = userService.getById(message.getFromUserId());
                if (user != null) {
                    message.setSenderUsername(user.getUsername());
                    message.setSenderNickname(user.getNickname());
                    message.setSenderAvatar(user.getAvatar());
                }
            }
        }
    }

    private void populateReceiverInfo(List<Message> messages) {
        for (Message message : messages) {
            if (message.getToUserId() != null) {
                User user = userService.getById(message.getToUserId());
                if (user != null) {
                    message.setReceiverUsername(user.getUsername());
                    message.setReceiverNickname(user.getNickname());
                    message.setReceiverAvatar(user.getAvatar());
                }
            }
        }
    }
}
