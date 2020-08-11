package com.flower.game.room;

import com.flower.game.dto.StartGameAction;
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
     * 房间缓存，key为玩家id
     */
    private static final Map<String, TeamRoom> Room_Gamer_Map = new ConcurrentHashMap<>();

    /**
     * 开房
     * @param startGameAction
     * @param gamerIds
     * @return
     */
    public boolean openRoom(String roomId, StartGameAction startGameAction, Collection<String> gamerIds) {
        TeamRoom room = new TeamRoom(startGameAction, new ArrayList<>(gamerIds));
        Room_Map.put(roomId, room);
        gamerIds.forEach(gamerId -> Room_Gamer_Map.put(gamerId, room));
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

    /**
     * 进入房间
     * @param roomId
     * @param gamerId
     */
    public void enter(String roomId, String gamerId) {
        TeamRoom room = Room_Map.get(roomId);
        if (room != null) {
            room.addGamer(gamerId);
        }
    }

    /**
     * 根据玩家id获取房间的信息
     * @param gamerId
     * @return
     */
    public StartGameAction getRoomByGamerId(String gamerId) {
        TeamRoom room = Room_Gamer_Map.get(gamerId);
        if (room != null) return room.getStartGameAction();
        return null;
    }
}
