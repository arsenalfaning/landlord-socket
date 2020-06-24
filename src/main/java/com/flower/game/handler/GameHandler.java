package com.flower.game.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.game.dto.RoomInfo;
import com.flower.game.socket.*;
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

    private static final RoomInfo Room_Info = new RoomInfo();

//    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    private final ObjectMapper objectMapper;
    private final SocketRegister socketRegister;

    public GameHandler(ObjectMapper objectMapper, SocketRegister socketRegister) {
        this.objectMapper = objectMapper;
        this.socketRegister = socketRegister;
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
                String text = message.getPayloadAsText();
                SocketIn socketIn = readTextMessage(text);
                if (socketIn != null) {
                    if (SocketConst.CMD_ROOM.equals(socketIn.getCmd())) {

                    }
                }
            }
            return Mono.just(webSocketSession.textMessage("ok"));
        });

        Mono<Void> output = webSocketSession.send(Flux.create(sink -> {
            SocketSender socketSender = new SocketSender(webSocketSession, sink);
            socketRegister.register(gamerId, socketSender);
            Room_Info.addGamer(gamerId);
        }));

        return Mono.zip(webSocketSession.send(common), output).then();
    }

    @Override
    public CorsConfiguration getCorsConfiguration(ServerWebExchange serverWebExchange) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        return configuration;
    }

    public SocketIn<String> readTextMessage(String text) {
        try {
            return objectMapper.readValue(text, new TypeReference<SocketIn<String>> (){});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public <T> SocketIn<T> readObjectMessage(String text, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(text, new TypeReference<SocketIn<T>> (){});
    }
}
