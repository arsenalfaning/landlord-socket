package com.flower.game.room;

import com.flower.game.dto.GameFrame;
import com.flower.game.dto.GamerBean;
import com.flower.game.dto.RoomBean;
import com.flower.game.game.Game;
import com.flower.game.service.GamerPointService;
import com.flower.game.util.SpringContextHolder;

import java.util.*;

public class TeamRoom implements RoomInterface{

    /**
     * 游戏结束
     */
    static final String End_Action = "6";
    static final String Room_Action = "0";
    static final String Action_Key = "action";

    private List<String> gamers;
    private String roomId;
    private Game game;

    public TeamRoom(String roomId, List<String> gamers) {
        this.roomId = roomId;
        this.gamers = gamers;
        Collections.shuffle(this.gamers);
        this.game = new Game();
        this.addInitFrame();
    }

    /**
     * 处理action
     * @param action
     */
    public void addAction(Map action) {
        this.game.receiveAction(action);
        this.sendFrameTask();
    }

    /**
     * 添加初始化帧
     */
    private void addInitFrame() {
        GamerPointService gamerPointService = SpringContextHolder.getBean(GamerPointService.class);
        RoomBean room = new RoomBean();
        room.setSeed(System.currentTimeMillis());
        room.setGamers(new LinkedList<>());
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
        return hasGamer(gamerId);
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
}