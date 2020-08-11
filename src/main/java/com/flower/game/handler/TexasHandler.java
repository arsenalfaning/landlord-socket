package com.flower.game.handler;

import com.flower.game.room.TeamRoomService;
import com.flower.game.socket.SocketRegister;
import com.flower.game.socket.SocketSender;
import com.flower.game.socket.SocketUtil;
import com.flower.game.util.JsonUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class TexasHandler implements WebSocketHandler, CorsConfigurationSource {

    private final TeamRoomService teamRoomService;
    private final SocketRegister socketRegister;

    public TexasHandler(TeamRoomService teamRoomService, SocketRegister socketRegister) {
        this.teamRoomService = teamRoomService;
        this.socketRegister = socketRegister;
    }

    @Override
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        return configuration;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        final String gamerId = SocketUtil.getGamerIdByQuery(session);
        final String roomId = SocketUtil.getRoomIdByQuery(session);
        Mono<Void> output = session.send(Flux.create(sink -> {
            SocketSender socketSender = new SocketSender(session, sink);
            socketRegister.register(gamerId, socketSender);
        }));
        Mono<Void> input = session.receive().doOnSubscribe(s -> {
            //连接开始
            log("TexasHandler subscribe");
            SocketUtil.setGamerIdByAttribute(session, gamerId);
            teamRoomService.enter(roomId, gamerId);
        }).doOnCancel(() -> {
            //连接结束
            log("TexasHandler cancel");
        }).doOnError(e -> {
            //出错
            log("TexasHandler error");
            log(e);
        }).doOnTerminate(() -> {
            //关闭连接
            log("TexasHandler terminate");
        }).doOnRequest(value -> {
            log("TexasHandler request");
        }).doOnComplete(() -> {
            log("TexasHandler complete");
            socketRegister.remove(gamerId);
        }).doOnNext(message -> {
            log("TexasHandler next");
        }).concatMap((message) -> {
            try {
                if (message.getType() ==  WebSocketMessage.Type.TEXT) {
                    String payload = message.getPayloadAsText();
                    teamRoomService.receiveAction(JsonUtil.readValue(payload, Map.class), roomId);
                }
                return Mono.empty();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Mono.empty();
        }).then();

        return Mono.zip(input, output).then();
    }

    private void log(Object object) {
        System.out.println(object);
    }
}
