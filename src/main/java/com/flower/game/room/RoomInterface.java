package com.flower.game.room;

import com.flower.game.dto.GameFrame;
import com.flower.game.landlord.cmd.CmdHolder;
import com.flower.game.runtime.GamePlay;
import com.flower.game.socket.SocketRegister;
import com.flower.game.util.SpringContextHolder;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

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
    default GamePlay getGamePlay() {
        return null;
    };

    /**
     * 广播推送
     * @param object
     */
    default void broadcast(Object object) {
        if (object instanceof GameFrame) {
            ((GameFrame) object).setSt(System.currentTimeMillis());
        }
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
        if (object instanceof GameFrame) {
            ((GameFrame) object).setSt(System.currentTimeMillis());
        }
        SocketRegister socketRegister = SpringContextHolder.getBean(SocketRegister.class);
        CmdHolder cmdHolder = SpringContextHolder.getBean(CmdHolder.class);
        String value = cmdHolder.writeValue(object);
        if (!StringUtils.isEmpty(value)) {
            socketRegister.messageTo(gamerId, value);
        }
    }

    /**
     * 推送list
     * @param gamerId
     * @param list
     */
    default void messageTo(String gamerId, List list) {
        SocketRegister socketRegister = SpringContextHolder.getBean(SocketRegister.class);
        CmdHolder cmdHolder = SpringContextHolder.getBean(CmdHolder.class);
        for (Object object : list) {
            if (object instanceof GameFrame) {
                ((GameFrame) object).setSt(System.currentTimeMillis());
            }
            String value = cmdHolder.writeValue(object);
            if (!StringUtils.isEmpty(value)) {
                socketRegister.messageTo(gamerId, value);
            }
        }
    }
}
