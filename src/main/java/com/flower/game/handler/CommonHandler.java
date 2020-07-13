package com.flower.game.handler;

import com.flower.game.game.MatchService;
import com.flower.game.room.CommonRoomService;
import com.flower.game.socket.SocketRegister;
import com.flower.game.socket.SocketSender;
import com.flower.game.socket.SocketUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CommonHandler implements WebSocketHandler, CorsConfigurationSource {

    private final SocketRegister socketRegister;
    private final MatchService matchService;
    private final CommonRoomService commonRoomService;

    public CommonHandler(SocketRegister socketRegister, MatchService matchService, CommonRoomService commonRoomService) {
        this.socketRegister = socketRegister;
        this.matchService = matchService;
        this.commonRoomService = commonRoomService;
    }

    private void log(Object object) {
        System.out.println(object);
    }
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        final String gamerId = SocketUtil.getGamerIdByQuery(session);
        Mono<Void> output = session.send(Flux.create(sink -> {
            SocketSender socketSender = new SocketSender(session, sink);
            socketRegister.register(gamerId, socketSender);
        }));
        Mono<Void> input = session.receive().doOnSubscribe(s -> {
            //连接开始
            log("subscribe");
            SocketUtil.setGamerIdByAttribute(session, gamerId);
            matchService.addGamer(gamerId);
        }).doOnCancel(() -> {
            //连接结束
            log("cancel");
        }).doOnError(e -> {
            //出错
            log("error");
            log(e);
        }).doOnTerminate(() -> {
            //关闭连接
            log("terminate");
        }).doOnRequest(value -> {
            log("request");
        }).doOnComplete(() -> {
            log("complete");
            socketRegister.remove(gamerId);
            matchService.removeGamer(gamerId);
        }).doOnNext(message -> {
            log("next");
        }).concatMap((message) -> {
            try {
                if (message.getType() ==  WebSocketMessage.Type.TEXT) {
                    String payload = message.getPayloadAsText();
                    commonRoomService.receiveAction(payload, gamerId);
                }
                return Mono.empty();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Mono.empty();
        }).then();

        return Mono.zip(input, output).then();
    }

    @Override
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        return configuration;
    }
}
