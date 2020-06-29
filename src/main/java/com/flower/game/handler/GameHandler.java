package com.flower.game.handler;

import com.flower.game.landlord.cmd.CmdHolder;
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
public class GameHandler implements WebSocketHandler, CorsConfigurationSource {

//    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    private final SocketRegister socketRegister;
    private final CmdHolder cmdHolder;

    public GameHandler(SocketRegister socketRegister, CmdHolder cmdHolder) {
        this.socketRegister = socketRegister;
        this.cmdHolder = cmdHolder;
    }

    private void log(Object object) {
        System.out.println(object);
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        final String gamerId = SocketUtil.getGamerIdByQuery(webSocketSession);
        Flux<WebSocketMessage> common = webSocketSession.receive().doOnSubscribe(s -> {
            //连接开始
            log("subscribe");
            SocketUtil.setGamerIdByAttribute(webSocketSession, gamerId);
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
        }).doOnNext(message -> {
            log("next");
        }).concatMap(message -> {
            log("concatMap");
            if (message.getType() ==  WebSocketMessage.Type.TEXT) {
                return Mono.just( webSocketSession.textMessage(cmdHolder.execute(message.getPayloadAsText(), gamerId)) );
            }
            return Mono.just(webSocketSession.textMessage("error"));
        });

        Mono<Void> output = webSocketSession.send(Flux.create(sink -> {
            SocketSender socketSender = new SocketSender(webSocketSession, sink);
            socketRegister.register(gamerId, socketSender);
        }));
        return Mono.zip(webSocketSession.send(common), output).then();
    }

    @Override
    public CorsConfiguration getCorsConfiguration(ServerWebExchange serverWebExchange) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        return configuration;
    }

}
