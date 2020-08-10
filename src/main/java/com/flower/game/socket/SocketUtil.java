package com.flower.game.socket;

import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SocketUtil {

    public static String getGamerIdByQuery(WebSocketSession webSocketSession) {
        return parseUserByURI(webSocketSession).get(SocketConst.PARAM_TOKEN);
    }

    public static String getRoomIdByQuery(WebSocketSession webSocketSession) {
        return parseUserByURI(webSocketSession).get(SocketConst.PARAM_ROOM);
    }

    public static String getGamerIdByAttribute(WebSocketSession webSocketSession) {
        return webSocketSession.getAttributes().get(SocketConst.PARAM_TOKEN).toString();
    }

    public static void setGamerIdByAttribute(WebSocketSession webSocketSession, String gamerId) {
        webSocketSession.getAttributes().put(SocketConst.PARAM_TOKEN, gamerId);
    }

    private static Map<String, String> parseUserByURI(WebSocketSession session){
        Map<String, String> map = new HashMap<>();
        String[] params = Optional.ofNullable(session.getHandshakeInfo().getUri().getQuery()).orElse("").split("&");
        for (String param : params) {
            String[] temp = param.split("=");
            if(temp.length == 2){
                map.put(temp[0],temp[1]);
            }
        }
        return map;
    }
}
