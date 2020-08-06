package com.flower.game.socket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
public class SocketRegister {

    private Map<String, SocketSender> SOCKET_SESSION_MAP = new ConcurrentSkipListMap<>();

    /**
     * 注册新用户
     * @param socketSender
     */
    public void register(String gamerId, SocketSender socketSender) {
        SOCKET_SESSION_MAP.put(gamerId, socketSender);
    }

    /**
     * 用户下线
     */
    public void remove(String gamerId) {
        SOCKET_SESSION_MAP.remove(gamerId);
    }

    /**
     * 广播消息
     * @param gamerIds
     * @param text
     */
    public void broadcast(Collection<String> gamerIds, String text) {
        if (gamerIds.isEmpty()) {
            SOCKET_SESSION_MAP.values().stream().forEach(s -> s.send(text));
        } else {
            gamerIds.stream().forEach(e -> {
                SocketSender socketSender = SOCKET_SESSION_MAP.get(e);
                if (socketSender != null) {
                    socketSender.send(text);
                }
            });
        }
    }

    /**
     * 单独给某个用户发消息
     * @param gamerId
     * @param text
     */
    public void messageTo(String gamerId, String text) {
        SocketSender socketSender = SOCKET_SESSION_MAP.get(gamerId);
        if (socketSender != null) {
            socketSender.send(text);
        }
    }

    /**
     * 获取session
     * @param gamerId
     * @return
     */
    public WebSocketSession getSession(String gamerId) {
        SocketSender socketSender = SOCKET_SESSION_MAP.get(gamerId);
        if (socketSender != null) {
            return socketSender.getWebSocketSession();
        }
        return null;
    }
}
