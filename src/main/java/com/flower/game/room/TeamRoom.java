package com.flower.game.room;

import com.flower.game.dto.*;
import com.flower.game.game.Game;
import com.flower.game.service.GamerPointService;
import com.flower.game.util.JsonUtil;
import com.flower.game.util.ScheduleUtil;
import com.flower.game.util.SpringContextHolder;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class TeamRoom implements RoomInterface{


    static final String Room_Action = "room";
    static final String Settle_Action = "settle";//结算单局积分
    static final String Next_Action = "next";//继续下一局
    static final String Action_Key = "action";

    private List<String> gamers;
    private String roomId;
    private String captainId;//队长id
    private Integer buttonIndex;//庄位idnex
    private Game game;
    private StartGameAction startGameAction;
    private volatile boolean running = true;//是否正在进行游戏
    private Set<String> seedSet = new ConcurrentSkipListSet<>();

    public TeamRoom(StartGameAction startGameAction, List<String> gamers) {
        this.startGameAction = startGameAction;
        this.gamers = gamers;
        this.captainId = gamers.get(0);
        Collections.shuffle(this.gamers);
        this.game = new Game();
        this.buttonIndex = -1;
        this.addInitFrame();
    }

    /**
     * 处理action
     * @param action
     */
    public synchronized void addAction(Map action) {
        if (Settle_Action.equals(action.get(Action_Key))) {
            TexasSettleAction settleAction = JsonUtil.readValue(JsonUtil.toString(action), TexasSettleAction.class);
            if (!seedSet.contains(settleAction.getData().getSeed())) {
                GamerPointService gamerPointService = SpringContextHolder.getBean(GamerPointService.class);
                settleAction.getData().getGamerResult().forEach(gb -> {
                    gamerPointService.modifyGamerPoint(gb.getGamerId(), gb.getPoint());
                });
                seedSet.add(settleAction.getData().getSeed());
            }
            running = false;
            return;
        } else if (Next_Action.equals(action.get(Action_Key))) {
            if (!running) {
                game.clearFrame();
                broadcast(addInitFrame());
            }
            return;
        }
        this.game.receiveAction(action);
        this.sendFrameTask();
    }

    /**
     * 添加初始化帧
     */
    private GameFrame addInitFrame() {
        GamerPointService gamerPointService = SpringContextHolder.getBean(GamerPointService.class);
        RoomBean room = new RoomBean();
        room.setSeed(System.currentTimeMillis());
        room.setGamers(new LinkedList<>());
        room.setCaptain(captainId);
        buttonIndex ++;
        if (buttonIndex >= gamers.size()) {
            buttonIndex = 0;
        }
        room.setButtonIndex(buttonIndex);
        for (byte i = 0; i < 52; i ++) {
            room.getCards().add(i);
        }
        Collections.shuffle(room.getCards());
        for (int i = 0; i < gamers.size(); i ++) {
            GamerBean gb = new GamerBean();
            gb.setGamerId(gamers.get(i));
            gb.setOrder(i);
            gb.setPoint(gamerPointService.getPointByGamerId(gb.getGamerId()));
            room.getGamers().add(gb);
        }
        Map<String, Object> action = new HashMap<>();
        action.put("action", Room_Action);
        action.put("data", room);
        this.game.receiveAction(action);
        return this.game.buildGameFrame();
    }

    /**
     * 发送帧任务
     */
    private void sendFrameTask() {
        GameFrame frame = game.buildGameFrame();
        broadcast(frame);
    }

    @Override
    public boolean addGamer(String gamerId) {
        boolean flag = hasGamer(gamerId);
        if (flag) {
            ScheduleUtil.addDelayTask(() -> this.messageTo(gamerId, game.allGameFrames()), 1);
        }
        return flag;
    }

    @Override
    public boolean removeGamer(String gamerId) {
        return false;
    }

    @Override
    public boolean hasGamer(String gamerId) {
        return gamers.contains(gamerId);
    }

    @Override
    public Collection<String> allGamers() {
        return gamers;
    }

    public StartGameAction getStartGameAction() {
        return startGameAction;
    }
}
