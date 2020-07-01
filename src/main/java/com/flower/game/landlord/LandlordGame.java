package com.flower.game.landlord;

import com.flower.game.landlord.util.OutUtil;
import com.flower.game.landlord.vo.GamerApprove;
import com.flower.game.landlord.vo.GamerPlay;
import com.flower.game.landlord.vo.GamerResultVo;
import com.flower.game.room.RoomInterface;
import com.flower.game.runtime.GamePlay;
import com.flower.game.runtime.GameRuntime;
import com.flower.game.runtime.GameUtil;
import com.flower.game.runtime.GamerRuntime;
import com.flower.game.socket.SocketConst;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class LandlordGame implements GamePlay {

    private static final String Play_History = "play_history";
    private static final String Landlord_Rest = "landlord_rest";//地主的底牌，存储List<Byte>
    private static final String Landlord_Index = "landlord_index";//抢地主索引，轮换，存储的0~2
    private static final String Landlord = "landlord";//地主玩家，存储的是Gamer的order
    private static final String Approve_History = "approve_history";//抢地主的历史记录，存储的GamerApprove

    public static final void addApproveHistory(GameRuntime gameRuntime, GamerApprove gamerApprove) {
        getApproveHistory(gameRuntime).put(gamerApprove.getPlayOrder(), gamerApprove);
    }

    public static final LinkedHashMap<Byte, GamerApprove> getApproveHistory(GameRuntime gameRuntime) {
        LinkedHashMap<Byte, GamerApprove> map = (LinkedHashMap<Byte, GamerApprove>) gameRuntime.dataMap.get(Approve_History);
        if (map == null) {
            map = new LinkedHashMap<>();
            gameRuntime.dataMap.put(Approve_History, map);
        }
        return map;
    }

    public static final void clearApproveHistory(GameRuntime gameRuntime) {
        gameRuntime.dataMap.remove(Approve_History);
    }

    public static final void setLandlord(GameRuntime gameRuntime, byte order) {
        gameRuntime.dataMap.put(Landlord, order);
    }

    public static final Byte getLandlord(GameRuntime gameRuntime) {
        return (Byte) gameRuntime.dataMap.get(Landlord);
    }

    public static final void setLandlordIndex(GameRuntime gameRuntime, byte index) {
        gameRuntime.dataMap.put(Landlord_Index, index);
    }
    public static final byte getLandlordIndex(GameRuntime gameRuntime) {
        Byte index = (Byte) gameRuntime.dataMap.get(Landlord_Index);
        if (index == null) {
            index = 0;
            setLandlordIndex(gameRuntime, index);
        }
        return index;
    }

    public static final void setLandlordRest(GameRuntime gameRuntime, List<Byte> rest) {
        gameRuntime.dataMap.put(Landlord_Rest, rest);
    }
    public static final List<Byte> getLandlordRest(GameRuntime gameRuntime) {
        return (List<Byte>) gameRuntime.dataMap.get(Landlord_Rest);
    }
    public static final void removeLandlordRest(GameRuntime gameRuntime) {
        gameRuntime.dataMap.remove(Landlord_Rest);
    }
    public static final List<GamerPlay> playHistory(GameRuntime gameRuntime) {
        List<GamerPlay> history = (List<GamerPlay>) gameRuntime.dataMap.get(Play_History);
        if (history == null) {
            history = new LinkedList<>();
            gameRuntime.dataMap.put(Play_History, history);
        }
        return history;
    }
    public static final void clearPlayHistory(GameRuntime gameRuntime) {
        gameRuntime.dataMap.remove(Play_History);
    }

    private final GameRuntime gameRuntime = new GameRuntime();

    private RoomInterface room;

    public LandlordGame(RoomInterface room) {
        this.room = room;
        gameRuntime.gamerRuntimeList = new ArrayList<>(3);
        gameRuntime.dataMap = new HashMap<>();
        this.init();
    }

    @Override
    public void init() {
        gameRuntime.status = GameUtil.Game_Status_Init;
        clearPlayHistory(gameRuntime);
        removeLandlordRest(gameRuntime);
        gameRuntime.gamerRuntimeList.stream().forEach(e -> {
            e.cards = null;
            e.ready = false;
        });
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
            gameRuntime.status = GameUtil.Game_Status_Before_Playing; //进入抢地主阶段
            gameRuntime.cards = GameUtil.allCards();
            this.shuffle();
            gameRuntime.gamerRuntimeList.get(0).cards = gameRuntime.cards.subList(0, 17);
            gameRuntime.gamerRuntimeList.get(1).cards = gameRuntime.cards.subList(17, 34);
            gameRuntime.gamerRuntimeList.get(2).cards = gameRuntime.cards.subList(34, 51);
            setLandlordRest(gameRuntime, gameRuntime.cards.subList(51, 54));
            this.sort();
            this.turn();
        }
        return true;
    }

    @Override
    public boolean sort() {
        Collections.sort(gameRuntime.gamerRuntimeList.get(0).cards, new LandlordSortComparator());
        Collections.sort(gameRuntime.gamerRuntimeList.get(1).cards, new LandlordSortComparator());
        Collections.sort(gameRuntime.gamerRuntimeList.get(2).cards, new LandlordSortComparator());
        Collections.sort(getLandlordRest(gameRuntime), new LandlordSortComparator());
        return true;
    }

    /**
     * 抢地主
     * @param gamerId
     * @return
     */
    public boolean approve(String gamerId, boolean value) {
        //1.是否可以开始抢
        if (!gameRuntime.status.equals(GameUtil.Game_Status_Before_Playing)) {
            return false;
        }
        //2.检查是否轮到抢
        GamerRuntime myself = gameRuntime.gamerRuntimeList.get(gameRuntime.playOrder);
        if (!myself.gamerId.equals(gamerId)) {
            return false;
        }
        //3.执行抢地主
        GamerApprove ga = new GamerApprove(gameRuntime.playOrder, value);
        addApproveHistory(gameRuntime, ga);
        this.turn();
        this.pushForApprove(SocketConst.CMD_UPDATE, ga);
        if (gameRuntime.status == GameUtil.Game_Status_Playing) {
            removeLandlordRest(gameRuntime);
        }
        return true;
    }

    @Override
    public void turn() {
        if (gameRuntime.playDeadline == null) {
            Byte order = getLandlordIndex(gameRuntime);
            gameRuntime.playOrder = order; //庄家先抢
            setLandlordIndex(gameRuntime, OutUtil.nextOrder(order)); //轮流坐庄
        } else {
            if (GameUtil.Game_Status_Before_Playing.equals(gameRuntime.status)) {//抢地主中
                /**
                 * 1.如果庄家未抢，则最多抢三轮，否则抢四轮
                 * 2.最后一个抢的当地主
                 */
                byte newOrder = OutUtil.nextOrder(gameRuntime.playOrder);
                byte landlordIndex = getLandlordIndex(gameRuntime);
                LinkedHashMap<Byte, GamerApprove> approves = getApproveHistory(gameRuntime);
                if (approves.keySet().size() == 3) {
                    if (landlordIndex == gameRuntime.playOrder) { //当前庄家刚抢完
                        newOrder = lastApprove(approves);
                        confirmLandlord(newOrder);
                    } else {
                        GamerApprove ap = approves.get(landlordIndex);
                        if (!ap.isValue()) { //庄家未抢
                            Byte no = lastApprove(approves);
                            if (no == null) { //没人抢地主
                                this.deal();//重新发牌
                                return;
                            }
                            newOrder = no;
                            confirmLandlord(newOrder);
                        }
                    }
                }
                gameRuntime.playOrder = newOrder;
            } else if (GameUtil.Game_Status_Playing.equals(gameRuntime.status)) {//出牌中
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
        }
        gameRuntime.playDeadline = turnDeadline();
    }

    /**
     * 确定地主
     */
    private void confirmLandlord(byte order) {
        setLandlord(gameRuntime, order);
        gameRuntime.gamerRuntimeList.get(order).cards.addAll(getLandlordRest(gameRuntime));
        Collections.sort(gameRuntime.gamerRuntimeList.get(order).cards, new LandlordSortComparator());
        gameRuntime.status = GameUtil.Game_Status_Playing;
        gameRuntime.playOrder = order;
    }

    private Byte lastApprove(LinkedHashMap<Byte, GamerApprove> approves) {
        GamerApprove last = null;
        for (GamerApprove a : approves.values()) {
            if (a.isValue()) {
                last = a;
            }
        }
        if (last != null) {
            return last.getPlayOrder();
        }
        return null;
    }

    @Override
    synchronized public boolean play(List<Byte> cards, String gamerId) {
        //-1.是否可以开始出牌
        if (!gameRuntime.status.equals(GameUtil.Game_Status_Playing)) {
            return false;
        }
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
            if (!playHistory.isEmpty()) {
                GamerPlay gamerPlay = playHistory.get(playHistory.size() - 1);
                //3.检查出的牌是否比上一手大
                if (gamerPlay.getLandlordCards().compareTo(landlordCards) < 0) {
                    playHistory.add(newPlay);
                    flag = true;
                }
            } else {
                playHistory.add(newPlay);
                flag = true;
            }
        }
        if (flag) {//4.执行出牌
            myCardsSet.removeAll(someCardsSet);
            myself.cards = new ArrayList<>(myCardsSet);
            Collections.sort(myself.cards, new LandlordSortComparator());
            this.turn();
            pushForPlay(SocketConst.CMD_UPDATE, newPlay);
            if (myself.cards.isEmpty()) {
                this.complete();
            }
        }
        return flag;
    }

    @Override
    public boolean complete() {
        //1.找到获胜方并创建结果
        List<GamerResultVo> resultVos = gameRuntime.gamerRuntimeList.stream().map(e -> {
            GamerResultVo vo = new GamerResultVo();
            vo.setWin(e.cards.isEmpty());
            if (vo.getWin()) {
                vo.setDelta(BigDecimal.TEN);
            } else {
                vo.setDelta(BigDecimal.TEN.multiply(BigDecimal.valueOf(-1)));
            }
            vo.setGamerId(e.gamerId);
            vo.setOrder(e.order);
            return vo;
        }).collect(Collectors.toList());
        pushResult(resultVos);
        //2.重置
        this.init();
        return true;
    }

    /**
     * 建议出牌
     * @return
     */
    public List<Byte> suggest(String gamerId) {
        List<GamerPlay> gamerPlays = playHistory(gameRuntime);
        GamerRuntime myself = getGamerByGamerId(gamerId);
        LandlordCards result = null;
        if (gamerPlays != null && !gamerPlays.isEmpty()) {
            result = LandlordUtil.suggestCards( LandlordUtil.convertCards(myself.cards), gamerPlays.get(gamerPlays.size() - 1).getLandlordCards());
        } else {
            result = LandlordUtil.suggestCards( LandlordUtil.convertCards(myself.cards), null);
        }
        if (result != null) {
            return result.toCards();
        }
        return null;
    }

    private GamerRuntime getGamerByGamerId(String gamerId) {
        return gameRuntime.gamerRuntimeList.stream().filter(e -> e.gamerId.equals(gamerId)).collect(Collectors.toList()).get(0);
    }

    private void pushResult(List<GamerResultVo> resultVos) {
        gameRuntime.gamerRuntimeList.stream().forEach(e -> {
            room.messageTo(e.gamerId, OutUtil.toResultVo(resultVos, e.gamerId, SocketConst.CMD_OVER));
        });
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

    private void pushForApprove(String cmd, GamerApprove gamerApprove) {
        gameRuntime.gamerRuntimeList.stream().forEach(e -> {
            room.messageTo(e.gamerId, OutUtil.toGameVoForApprove(gameRuntime, e.gamerId, cmd, gamerApprove));
        });
    }

    private LocalDateTime turnDeadline() {
        if (gameRuntime.status.equals(GameUtil.Game_Status_Playing)) {
            return LocalDateTime.now().plus(Duration.ofSeconds(15));
        }
        return LocalDateTime.now().plus(Duration.ofSeconds(5));
    }
}
