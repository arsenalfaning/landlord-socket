package com.flower.game.room;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TeamRoomService {

    /**
     * 房间缓存，key为房间id
     */
    private static final Map<String, TeamRoom> Room_Map = new ConcurrentHashMap<>();

    /**
     * 开房
     * @param roomId
     * @param gamerIds
     * @return
     */
    public boolean openRoom(String roomId, Collection<String> gamerIds) {
        TeamRoom room = new TeamRoom(roomId, new ArrayList<>(gamerIds));
        Room_Map.put(roomId, room);
        return true;
    }

    /**
     * 处理action
     * @param action
     * @param roomId
     */
    public void receiveAction(Map action, String roomId) {
        TeamRoom room = Room_Map.get(roomId);
        if (room != null) {
            room.addAction(action);
        }
    }
}
