package com.flower.game.room;

import com.flower.game.dto.GameFrame;
import com.flower.game.dto.GamerBean;
import com.flower.game.dto.RoomBean;
import com.flower.game.game.Game;
import com.flower.game.util.ScheduleUtil;
import reactor.core.Disposable;

import java.util.*;

public class CommonRoom implements RoomInterface{

    private LinkedHashSet<String> gamersSet;
    private List<String> gamers;
    private Game game;
    /**
     * 定时发送帧任务
     */
    private Disposable scheduleTask;

    public CommonRoom(List<String> gamers) {
        this.gamersSet = new LinkedHashSet<>(gamers);
        this.gamers = new ArrayList<>(gamers);
        this.game = new Game();
        this.addInitFrame();
//        this.scheduleTask = ScheduleUtil.addIntervalTask(() -> this.sendFrameTask(), 5000);
    }

    public void addAction(Map action) {
        this.game.receiveAction(action);
        this.sendFrameTask();
    }

    /**
     * 添加初始化帧
     */
    private void addInitFrame() {
        RoomBean room = new RoomBean();
        room.setSeed(System.currentTimeMillis());
        room.setGamers(new ArrayList<>(3));
        for (int i = 0; i < gamers.size(); i ++) {
            GamerBean gb = new GamerBean();
            gb.setGamerId(gamers.get(i));
            gb.setOrder(i);
            gb.setPoint(10000L);
            room.getGamers().add(gb);
        }
        Map<String, Object> action = new HashMap<>();
        action.put("action", 0);
        action.put("data", room);
        this.game.receiveAction(action);
        ScheduleUtil.addDelayTask(() -> this.sendFrameTask(), 1);
    }

    /**
     * 发送帧任务
     */
    private void sendFrameTask() {
        GameFrame frame = game.buildGameFrame();
        broadcast(frame);
    }

    /**
     * 关闭房间
     */
    public void close() {
        scheduleTask.dispose();
    }

    @Override
    public boolean addGamer(String gamerId) {
        if (gamersSet.contains(gamerId)) {
            ScheduleUtil.addDelayTask(() -> this.messageTo(gamerId, game.allGameFrames()), 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeGamer(String gamerId) {
        return false;
    }

    @Override
    public boolean hasGamer(String gamerId) {
        return gamersSet.contains(gamerId);
    }

    @Override
    public Collection<String> allGamers() {
        return gamers;
    }
}
