package com.flower.game.room;

import com.flower.game.landlord.cmd.CmdHolder;
import com.flower.game.runtime.GamePlay;
import com.flower.game.socket.SocketRegister;
import com.flower.game.util.SpringContextHolder;
import org.springframework.util.StringUtils;

import java.util.Collection;

public interface RoomInterface {

    /**
     * 添加玩家，支持旧玩家重连
     * @param gamerId
     * @return
     */
    boolean addGamer(String gamerId);

    /**
     * 删除玩家
     * @param gamerId
     * @return
     */
    boolean removeGamer(String gamerId);

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
     * 获取gameplay
     * @return
     */
    GamePlay getGamePlay();

    /**
     * 广播推送
     * @param object
     */
    default void broadcast(Object object) {
        SocketRegister socketRegister = SpringContextHolder.getBean(SocketRegister.class);
        CmdHolder cmdHolder = SpringContextHolder.getBean(CmdHolder.class);
        String value = cmdHolder.writeValue(object);
        if (!StringUtils.isEmpty(value)) {
            socketRegister.broadcast(allGamers(), value);
        }
    }

    /**
     * 单个推送
     * @param object
     * @param gamerId
     */
    default void messageTo(String gamerId, Object object) {
        SocketRegister socketRegister = SpringContextHolder.getBean(SocketRegister.class);
        CmdHolder cmdHolder = SpringContextHolder.getBean(CmdHolder.class);
        String value = cmdHolder.writeValue(object);
        if (!StringUtils.isEmpty(value)) {
            socketRegister.messageTo(gamerId, value);
        }
    }
}
