package com.flower.game.landlord.util;

import com.flower.game.landlord.vo.GameVo;
import com.flower.game.landlord.vo.GamerVo;
import com.flower.game.runtime.GameRuntime;
import com.flower.game.runtime.GameUtil;
import com.flower.game.runtime.GamerRuntime;
import com.flower.game.socket.SocketOut;

import java.util.HashMap;
import java.util.Map;

public class OutUtil {

    /**
     * 把游戏运行数据转化为客户端容易处理的vo
     * @param gameRuntime
     * @param gamerId
     * @return
     */
    public static SocketOut<GameVo> toGameVo(GameRuntime gameRuntime, String gamerId, String cmd) {
        final GameVo gameVo = new GameVo();
        Map<Byte, GamerVo> voMap = new HashMap<>();
        byte myOrder = -1;
        for (GamerRuntime gr : gameRuntime.gamerRuntimeList) {
            GamerVo vo = toGamerVo(gr, gameRuntime);
            if (gr.gamerId.equals(gamerId)) {
                gameVo.setMyself(vo);
                myOrder = gr.order;
            } else {
                voMap.put(gr.order, vo);
            }
        }
        gameVo.setPrev(voMap.get(prevOrder(myOrder)));
        gameVo.setNext(voMap.get(nextOrder(myOrder)));
        gameVo.setPlayingGamer(gameRuntime.turnGamerId);
        gameVo.setPlayingDeadline(gameRuntime.turnDeadline);
        gameVo.setStatus(gameRuntime.status);
        return SocketOut.ok(gameVo, cmd);
    }

    private static byte prevOrder(byte order) {
        byte prev = (byte) (order - 1);
        if (prev < 0) {
            prev = 2;
        }
        return prev;
    }

    private static byte nextOrder(byte order) {
        byte next = (byte) (order + 1);
        if (next > 2) {
            next = 0;
        }
        return next;
    }

    private static GamerVo toGamerVo(GamerRuntime gamerRuntime, GameRuntime gameRuntime) {
        GamerVo vo = new GamerVo();
        vo.setCards(gamerRuntime.cards);
        vo.setCardsNumber(gamerRuntime.cards == null ? null : gamerRuntime.cards.size());
        if (gameRuntime.status.equals(GameUtil.Game_Status_Init)) {
            vo.setReady(gamerRuntime.ready);
        }
        return vo;
    }
}
