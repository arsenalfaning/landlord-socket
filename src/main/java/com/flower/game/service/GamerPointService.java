package com.flower.game.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家积分服务
 */
@Service
public class GamerPointService {

    private static final Map<String, Long> GAMER_POINT_MAP = new ConcurrentHashMap<>();

    public Long getPointByGamerId(String gamerId) {
        if (GAMER_POINT_MAP.containsKey(gamerId)) {
            return GAMER_POINT_MAP.get(gamerId);
        }
        GAMER_POINT_MAP.put(gamerId, 10000L);
        return 1000L;
    }

    public boolean modifyGamerPoint(String gamerId, Long delta) {
        GAMER_POINT_MAP.put(gamerId, GAMER_POINT_MAP.get(gamerId) + delta);
        return true;
    }
}
