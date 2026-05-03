package com.industry.websocket;

import com.alibaba.fastjson2.JSON;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ServerEndpoint("/ws/device")
public class WebSocketServer {

    private static final ConcurrentHashMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    @OnOpen
    public void onOpen(Session session) {
        SESSION_MAP.put(session.getId(), session);
        int count = ONLINE_COUNT.incrementAndGet();
        log.info("WebSocket连接建立, sessionId: {}, 在线人数: {}", session.getId(), count);
    }

    @OnClose
    public void onClose(Session session) {
        SESSION_MAP.remove(session.getId());
        int count = ONLINE_COUNT.decrementAndGet();
        log.info("WebSocket连接关闭, sessionId: {}, 在线人数: {}", session.getId(), count);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到客户端消息: {}", message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误: {}", error.getMessage(), error);
    }

    public static void sendMessage(Session session, Object message) {
        try {
            String jsonMessage = JSON.toJSONString(message);
            session.getBasicRemote().sendText(jsonMessage);
        } catch (IOException e) {
            log.error("发送消息失败: {}", e.getMessage());
        }
    }

    public static void sendToAll(Object message) {
        String jsonMessage = JSON.toJSONString(message);
        SESSION_MAP.forEach((sessionId, session) -> {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(jsonMessage);
                } catch (IOException e) {
                    log.error("广播消息失败, sessionId: {}", sessionId);
                }
            }
        });
    }

    public static int getOnlineCount() {
        return ONLINE_COUNT.get();
    }
}
