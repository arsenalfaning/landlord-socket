package com.flower.game.room;

import com.flower.game.socket.SocketRegister;
import com.flower.game.util.SpringContextHolder;

import java.util.Collection;

public interface RoomInterface {

    /**
     * 添加玩家，支持旧玩家重连
     * @param gamerId
     * @return
     */
    String addGamer(String gamerId);

    /**
     * 查询玩家是否在该房间
     * @param gamerId
     * @return
     */
    boolean hasGamer(String gamerId);

    /**
     * 查询该房间所有玩家
     * @return
     */
    Collection<String> allGamers();

    /**
     * 广播推送
     * @param text
     */
    default void broadcast(String text) {
        SocketRegister socketRegister = SpringContextHolder.getBean(SocketRegister.class);
        socketRegister.broadcast(allGamers(), text);
    }
}
