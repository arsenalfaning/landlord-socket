package com.flower.game.room;

import com.flower.game.landlord.LandlordRoom;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class RoomService {

    /**
     * 房间map
     * key: 房间号
     */
    private static final Map<String, RoomInterface> ROOM_INFO_MAP = new ConcurrentSkipListMap<>();

    /**
     * 玩家加入房间
     * @param roomId
     * @param gamerId
     * @return
     */
    public String addGamer(String roomId, String gamerId) {
        RoomInterface room = ROOM_INFO_MAP.get(roomId);
        if (room == null) {
            //TODO 未来这些信息要从管理服务获取
            ROOM_INFO_MAP.put(roomId, new LandlordRoom());
//            return "无此房间";
        }
        return room.addGamer(gamerId);
    }
}
