package com.flower.game.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class CommonRoomService {

    /**
     * 玩家房间map
     * key: 玩家id
     */
    private static final Map<String, CommonRoom> GAMER_ROOM_MAP = new ConcurrentSkipListMap<>();

    private ObjectMapper objectMapper;

    public CommonRoomService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 开一个新房间
     * @param gamers
     */
    public void openRoom(List<String> gamers) {
        CommonRoom room = new CommonRoom(gamers);
        gamers.forEach(gamerId -> GAMER_ROOM_MAP.put(gamerId, room));
    }

    /**
     * 接收action
     * @param payload
     * @param gamerId
     */
    public void receiveAction(String payload, String gamerId) {
        try {
            CommonRoom room = GAMER_ROOM_MAP.get(gamerId);
            if (room != null) {
                Map action = objectMapper.readValue(payload, Map.class);
                room.addAction(action);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断线玩家重连
     * @param gamerId
     * @return
     */
    public boolean joinOldGame(String gamerId) {
        CommonRoom room = GAMER_ROOM_MAP.get(gamerId);
        if (room != null) {
            return room.addGamer(gamerId);
        }
        return false;
    }

    /**
     * 玩家离开房间
     * @param gamerId
     */
    public void removeGamer(String gamerId) {
        GAMER_ROOM_MAP.remove(gamerId);
    }
}
