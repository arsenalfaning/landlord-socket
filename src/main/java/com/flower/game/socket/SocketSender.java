package com.flower.game.socket;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

public class SocketSender {

    private WebSocketSession webSocketSession;
    private FluxSink<WebSocketMessage> fluxSink;

    public SocketSender(WebSocketSession webSocketSession, FluxSink<WebSocketMessage> fluxSink) {
        this.webSocketSession = webSocketSession;
        this.fluxSink = fluxSink;
    }

    public void send(String data) {
        fluxSink.next(webSocketSession.textMessage(data));
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }
}
