//package com.flower.game.service;
//
//import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import reactor.core.publisher.Mono;
//
//@Service
//public class RedisService {
//
//    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
//
//    public RedisService(ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
//        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
//    }
//
//    public Mono<Boolean> checkGamerIdValid(String gamerId) {
//        Mono<String> mono = reactiveStringRedisTemplate.opsForValue().get(gamerId);
//        return mono.map(e -> !StringUtils.isEmpty(e));
//    }
//}
