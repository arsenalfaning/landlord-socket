package com.flower.game.room;

import com.flower.game.landlord.LandlordRoom;
import com.flower.game.runtime.GamePlay;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class RoomService {

    /**
     * 房间map
     * key: 房间号
     */
    private static final Map<String, RoomInterface> ROOM_INFO_MAP = new ConcurrentSkipListMap<>();

    /**
     * 玩家房间map
     * key: 玩家id
     */
    private static final Map<String, RoomInterface> GAMER_ROOM_MAP = new ConcurrentSkipListMap<>();

    /**
     * 玩家加入房间
     * @param roomId
     * @param gamerId
     * @return
     */
    public Boolean addGamer(String roomId, String gamerId) {
        RoomInterface room = ROOM_INFO_MAP.get(roomId);
        if (room == null) {
            //TODO 未来这些信息要从管理服务获取'
            room = new LandlordRoom();
//            return "无此房间";
            ROOM_INFO_MAP.put(roomId, room);
        }
        if ( room.addGamer(gamerId) ) {
            GAMER_ROOM_MAP.put(gamerId, room);
            return true;
        }
        return false;
    }

    /**
     * 玩家准备
     * @param gamerId
     * @return
     */
    public boolean gamerReady(String gamerId) {
        RoomInterface room = GAMER_ROOM_MAP.get(gamerId);
        if (room != null) {
            return room.getGamePlay().ready(gamerId);
        }
        return false;
    }

    /**
     * 获取某玩家参与的游戏
     * @param gamerId
     * @return
     */
    public Optional<GamePlay> getGamePlayByGamerId(String gamerId) {
        RoomInterface room = GAMER_ROOM_MAP.get(gamerId);
        if (room != null) {
            return Optional.of(room.getGamePlay());
        }
        return Optional.empty();
    }

    /**
     * 玩家下线（连接断开）
     * @param gamerId
     */
    public void removeGamer(String gamerId) {
        GAMER_ROOM_MAP.remove(gamerId);
    }
}
