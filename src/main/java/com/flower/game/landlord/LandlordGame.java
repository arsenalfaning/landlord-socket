package com.flower.game.landlord;

import com.flower.game.landlord.util.OutUtil;
import com.flower.game.room.RoomInterface;
import com.flower.game.runtime.*;
import com.flower.game.socket.SocketConst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LandlordGame implements GamePlay {

    public final GameRuntime gameRuntime = new GameRuntime();

    private RoomInterface room;

    public LandlordGame(RoomInterface room) {
        this.room = room;
        this.init();
    }

    @Override
    public void init() {
        gameRuntime.dataMap = new HashMap<>();
        gameRuntime.gamerRuntimeList = new ArrayList<>(3);
        gameRuntime.status = GameUtil.Game_Status_Init;
    }

    @Override
    synchronized public boolean join(String gamerId) {
        if (gameRuntime.gamerRuntimeList.size() < 3) {
            GamerRuntime gamerRuntime = new GamerRuntime();
            gamerRuntime.order = (byte) gameRuntime.gamerRuntimeList.size();
            gamerRuntime.gamerId = gamerId;
            gameRuntime.gamerRuntimeList.add(gamerRuntime);
            push(SocketConst.CMD_UPDATE);
            return true;
        }
        return false;
    }

    @Override
    synchronized public boolean ready(String gamerId) {
        boolean result = false;
        for (GamerRuntime gr : gameRuntime.gamerRuntimeList) {
            if (gr.gamerId.equals(gamerId) && !Boolean.TRUE.equals(gr.ready)) {
                gr.ready = true;
                deal();
                result = true;
                break;
            }
        }
        if (result) {
            push(SocketConst.CMD_UPDATE);
        }
        return result;
    }

    @Override
    synchronized public boolean unReady(String gamerId) {
        if (gameRuntime.status == GameUtil.Game_Status_Init) {
            for (GamerRuntime gr : gameRuntime.gamerRuntimeList) {
                if (gr.gamerId.equals(gamerId)) {
                    gr.ready = false;
                    push(SocketConst.CMD_UPDATE);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shuffle() {
        GameUtil.shuffleCards(gameRuntime.cards);
        return true;
    }

    @Override
    public boolean cut() {
        return true;
    }

    @Override
    public boolean deal() {
        if ( gameRuntime.gamerRuntimeList.size() == 3 && gameRuntime.gamerRuntimeList.stream().allMatch(e -> Boolean.TRUE.equals(e.ready)) ) {
            gameRuntime.status = GameUtil.Game_Status_Playing;
            gameRuntime.cards = GameUtil.allCards();
            this.shuffle();
            gameRuntime.gamerRuntimeList.get(0).cards = gameRuntime.cards.subList(0, 18);
            gameRuntime.gamerRuntimeList.get(1).cards = gameRuntime.cards.subList(18, 36);
            gameRuntime.gamerRuntimeList.get(2).cards = gameRuntime.cards.subList(36, 54);
            this.sort();
        }
        return true;
    }

    @Override
    public boolean sort() {
        Collections.sort(gameRuntime.gamerRuntimeList.get(0).cards, new LandlordSortComparator());
        Collections.sort(gameRuntime.gamerRuntimeList.get(1).cards, new LandlordSortComparator());
        Collections.sort(gameRuntime.gamerRuntimeList.get(2).cards, new LandlordSortComparator());
        return true;
    }

    @Override
    synchronized public boolean play(PlayData playData) {
        //1.检查是否有牌
        //2.检查出牌是否满足规则
        //3.执行出牌
        return true;
    }

    @Override
    public boolean complete() {
        return true;
    }

    private void push(String cmd) {
        System.out.println("push");
        gameRuntime.gamerRuntimeList.stream().forEach(e -> {
            room.messageTo(e.gamerId, OutUtil.toGameVo(gameRuntime, e.gamerId, cmd));
        });
    }
}
