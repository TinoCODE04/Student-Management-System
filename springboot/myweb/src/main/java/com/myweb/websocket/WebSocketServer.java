package com.myweb.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务器
 */
@ServerEndpoint("/ws/{userId}")
@Component
@Slf4j
public class WebSocketServer {
    
    // 存储所有在线用户的连接
    private static final ConcurrentHashMap<String, Session> ONLINE_USERS = new ConcurrentHashMap<>();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 连接建立
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        ONLINE_USERS.put(userId, session);
        log.info("WebSocket connected: userId={}, total={}", userId, ONLINE_USERS.size());
    }
    
    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        ONLINE_USERS.remove(userId);
        log.info("WebSocket disconnected: userId={}, total={}", userId, ONLINE_USERS.size());
    }
    
    /**
     * 收到客户端消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        log.info("Received message from userId={}: {}", userId, message);
    }
    
    /**
     * 发生错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket error", error);
    }
    
    /**
     * 发送消息给指定用户
     */
    public void sendMessageToUser(String userId, Object message) {
        Session session = ONLINE_USERS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                session.getBasicRemote().sendText(json);
                log.info("Message sent to userId={}", userId);
            } catch (IOException e) {
                log.error("Error sending message to userId=" + userId, e);
            }
        } else {
            log.debug("User {} is not online, message not sent", userId);
        }
    }
    
    /**
     * 广播消息给所有在线用户
     */
    public void broadcast(Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            for (Session session : ONLINE_USERS.values()) {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(json);
                }
            }
            log.info("Message broadcasted to {} users", ONLINE_USERS.size());
        } catch (IOException e) {
            log.error("Error broadcasting message", e);
        }
    }
}
