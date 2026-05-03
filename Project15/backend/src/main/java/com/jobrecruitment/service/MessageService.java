package com.jobrecruitment.service;

import com.jobrecruitment.context.UserContext;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.entity.Message;
import com.jobrecruitment.entity.User;
import com.jobrecruitment.mapper.MessageMapper;
import com.jobrecruitment.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    public void sendSystemMessage(Long receiverId, String content) {
        Message message = new Message();
        message.setSenderId(0L);
        message.setReceiverId(receiverId);
        message.setMessageType(Message.TYPE_SYSTEM);
        message.setContent(content);
        message.setIsRead(0);
        messageMapper.insert(message);
        log.info("发送系统消息给用户 {}: {}", receiverId, content);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(Long receiverId, String content) {
        Long senderId = UserContext.getUserId();

        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMessageType(Message.TYPE_CHAT);
        message.setContent(content);
        message.setIsRead(0);
        messageMapper.insert(message);
    }

    public List<Message> listReceived(PageDTO dto) {
        Long userId = UserContext.getUserId();

        var query = messageMapper.createLambdaQuery()
                .andEq(Message::getReceiverId, userId);

        if (dto.getStatus() != null) {
            query.andEq(Message::getIsRead, dto.getStatus());
        }

        List<Message> messages = query.orderByDesc(Message::getCreateTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();

        messages.forEach(this::fillUserInfo);
        return messages;
    }

    public Long countUnread() {
        Long userId = UserContext.getUserId();
        return messageMapper.createLambdaQuery()
                .andEq(Message::getReceiverId, userId)
                .andEq(Message::getIsRead, 0)
                .count();
    }

    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long id) {
        Long userId = UserContext.getUserId();
        Message message = messageMapper.single(id);
        if (message != null && message.getReceiverId().equals(userId)) {
            message.setIsRead(1);
            messageMapper.updateById(message);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        Long userId = UserContext.getUserId();
        messageMapper.createLambdaQuery()
                .andEq(Message::getReceiverId, userId)
                .andEq(Message::getIsRead, 0)
                .createUpdate()
                .set(Message::getIsRead, 1)
                .execute();
    }

    public List<Message> listChat(Long otherUserId, PageDTO dto) {
        Long userId = UserContext.getUserId();

        var query = messageMapper.createLambdaQuery()
                .andEq(Message::getMessageType, Message.TYPE_CHAT)
                .and(q -> q
                        .andEq(Message::getSenderId, userId).andEq(Message::getReceiverId, otherUserId)
                        .or().andEq(Message::getSenderId, otherUserId).andEq(Message::getReceiverId, userId)
                );

        List<Message> messages = query.orderBy(Message::getCreateTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();

        messages.forEach(this::fillUserInfo);
        return messages;
    }

    private void fillUserInfo(Message message) {
        if (message.getSenderId() != null && message.getSenderId() > 0) {
            User sender = userMapper.single(message.getSenderId());
            if (sender != null) {
                message.setSenderName(sender.getUsername());
            }
        } else if (message.getSenderId() != null && message.getSenderId() == 0) {
            message.setSenderName("系统消息");
        }
    }
}
