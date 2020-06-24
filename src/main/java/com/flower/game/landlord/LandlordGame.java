package com.flower.game.landlord;

import com.flower.game.runtime.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@Service
public class LandlordGame implements GamePlay {

    public static final GameRuntime gameRuntime = new GameRuntime();

    @Override
    public void init() {
        gameRuntime.cards = GameUtil.allCards();
        gameRuntime.dataMap = new HashMap<>();
        gameRuntime.gamerRuntimeList = new ArrayList<>(3);
        gameRuntime.status = GameUtil.Game_Status_Init;
    }

    @Override
    public GameRuntime join(String gamerId) {
        if (gameRuntime.gamerRuntimeList.size() < 3) {
            GamerRuntime gamerRuntime = new GamerRuntime();
            gamerRuntime.order = (byte) gameRuntime.gamerRuntimeList.size();
            gamerRuntime.gamerId = gamerId;
        }
        return gameRuntime;
    }

    @Override
    public GameRuntime ready() {
        return null;
    }

    @Override
    public GameRuntime unReady() {
        return null;
    }

    @Override
    public GameRuntime shuffle() {
        GameUtil.shuffleCards(gameRuntime.cards);
        return gameRuntime;
    }

    @Override
    public GameRuntime cut() {
        return gameRuntime;
    }

    @Override
    public GameRuntime deal() {
        gameRuntime.gamerRuntimeList.get(0).cards = gameRuntime.cards.subList(0, 18);
        gameRuntime.gamerRuntimeList.get(1).cards = gameRuntime.cards.subList(18, 36);
        gameRuntime.gamerRuntimeList.get(2).cards = gameRuntime.cards.subList(36, 54);
        return gameRuntime;
    }

    @Override
    public GameRuntime sort() {
        Collections.sort(gameRuntime.gamerRuntimeList.get(0).cards, new LandlordSortComparator());
        Collections.sort(gameRuntime.gamerRuntimeList.get(1).cards, new LandlordSortComparator());
        Collections.sort(gameRuntime.gamerRuntimeList.get(2).cards, new LandlordSortComparator());
        return gameRuntime;
    }

    @Override
    public GameRuntime play(PlayData playData) {
        //1.检查是否有牌
        //2.检查出牌是否满足规则
        //3.执行出牌
        return gameRuntime;
    }

    @Override
    public GameRuntime complete() {
        return gameRuntime;
    }
}
