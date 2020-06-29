package com.flower.game.landlord;

import com.flower.game.landlord.util.OutUtil;
import com.flower.game.landlord.vo.GamerPlay;
import com.flower.game.room.RoomInterface;
import com.flower.game.runtime.GamePlay;
import com.flower.game.runtime.GameRuntime;
import com.flower.game.runtime.GameUtil;
import com.flower.game.runtime.GamerRuntime;
import com.flower.game.socket.SocketConst;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class LandlordGame implements GamePlay {

    private static final String Play_History = "play_history";

    public static final List<GamerPlay> playHistory(GameRuntime gameRuntime) {
        return (List<GamerPlay>) gameRuntime.dataMap.get(Play_History);
    }
    public static final void clearPlayHistory(GameRuntime gameRuntime) {
        gameRuntime.dataMap.remove(Play_History);
    }

    private final GameRuntime gameRuntime = new GameRuntime();

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
            this.turn();//TODO 未来添加抢地主功能
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
    public void turn() {
        if (gameRuntime.playDeadline == null) {
            gameRuntime.playOrder = 0;
        } else {
            byte newOrder = OutUtil.nextOrder(gameRuntime.playOrder);
            List<GamerPlay> playHistory = playHistory(gameRuntime);
            if (playHistory != null && !playHistory.isEmpty()) {
                GamerPlay gp = playHistory.get(playHistory.size() - 1);
                if (gp.getPlayOrder() == newOrder) {
                    clearPlayHistory(gameRuntime);
                }
            }
            gameRuntime.playOrder = newOrder;
        }
        gameRuntime.playDeadline = turnDeadline();
    }

    @Override
    synchronized public boolean play(List<Byte> cards, String gamerId) {
        //0.检查是否轮到出牌
        GamerRuntime myself = gameRuntime.gamerRuntimeList.get(gameRuntime.playOrder);
        if (!myself.gamerId.equals(gamerId)) {
            return false;
        }
        //1.检查是否有牌
        if (cards == null || cards.isEmpty()) {//要不起
            this.turn();
            push(SocketConst.CMD_UPDATE);
            return true;
        }
        Set<Byte> someCardsSet = new HashSet<>(cards);
        Set<Byte> myCardsSet = new HashSet<>(myself.cards);
        boolean flag = false;
        GamerPlay newPlay = null;
        if (myCardsSet.containsAll(someCardsSet) && someCardsSet.size() == cards.size()) {
            //2.检查出牌是否满足规则
            LandlordCards landlordCards = LandlordUtil.checkCards(LandlordUtil.convertCards(cards));
            if (landlordCards == null) { //不满足规则返回false
                return false;
            }
            newPlay = new GamerPlay(gameRuntime.playOrder, landlordCards, cards);
            List<GamerPlay> playHistory = playHistory(gameRuntime);
            if (playHistory != null && !playHistory.isEmpty()) {
                GamerPlay gamerPlay = playHistory.get(playHistory.size() - 1);
                //3.检查出的牌是否比上一手大
                if (gamerPlay.getLandlordCards().compareTo(landlordCards) < 0) {
                    playHistory.add(newPlay);
                    flag = true;
                }
            } else {
                playHistory = new LinkedList<>();
                playHistory.add(newPlay);
                gameRuntime.dataMap.put(Play_History, playHistory);
                flag = true;
            }
        }
        if (flag) {//4.执行出牌
            myCardsSet.removeAll(someCardsSet);
            myself.cards = new ArrayList<>(myCardsSet);
            Collections.sort(myself.cards, new LandlordSortComparator());
            this.turn();
            pushForPlay(SocketConst.CMD_UPDATE, newPlay);
        }
        return flag;
    }

    @Override
    public boolean complete() {
        return true;
    }

    private void push(String cmd) {
        gameRuntime.gamerRuntimeList.stream().forEach(e -> {
            room.messageTo(e.gamerId, OutUtil.toGameVo(gameRuntime, e.gamerId, cmd));
        });
    }

    private void pushForPlay(String cmd, GamerPlay gamerPlay) {
        gameRuntime.gamerRuntimeList.stream().forEach(e -> {
            room.messageTo(e.gamerId, OutUtil.toGameVoForPlay(gameRuntime, e.gamerId, cmd, gamerPlay));
        });
    }

    private LocalDateTime turnDeadline() {
        return LocalDateTime.now().plus(Duration.ofSeconds(15));
    }
}
