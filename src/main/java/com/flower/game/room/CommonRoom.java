package com.flower.game.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.game.dto.EndAction;
import com.flower.game.dto.GameFrame;
import com.flower.game.dto.GamerBean;
import com.flower.game.dto.RoomBean;
import com.flower.game.game.Game;
import com.flower.game.service.GamerPointService;
import com.flower.game.util.ScheduleUtil;
import com.flower.game.util.SpringContextHolder;
import reactor.core.Disposable;

import java.util.*;

public class CommonRoom implements RoomInterface{

    /**
     * 游戏结束
     */
    static final String End_Action = "6";
    static final String Action_Key = "action";

    private List<EndAction> result = Collections.synchronizedList(new ArrayList<>(3));
    private LinkedHashSet<String> gamersSet;
    private List<String> gamers;
    private Game game;
    private boolean over = false;
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

    /**
     * @param action
     * @return 是否结束
     * @throws JsonProcessingException
     */
    public void addAction(Map action) throws JsonProcessingException {
        if (action.get(Action_Key).toString().equals(End_Action)) {
            ObjectMapper om = SpringContextHolder.getBean(ObjectMapper.class);
            result.add(om.readValue(om.writeValueAsString(action), EndAction.class));
            if (result.size() >= 2) {//可以进行结算
                this.calculateResult();
                this.close();
                this.over = true;
            }
        }
        this.game.receiveAction(action);
        this.sendFrameTask();
    }

    private void calculateResult() {
        Map<String, Long> gamerPointMap = new HashMap<>();
        Map<String, Long> validGamerPointMap = new HashMap<>();
        this.result.forEach(ea -> {
            ea.getData().forEach(gb -> {
                Long p = gamerPointMap.get(gb.getGamerId());
                if (p == null) {
                    gamerPointMap.put(gb.getGamerId(), gb.getPoint());
                } else if (p.equals(gb.getPoint())) {
                    validGamerPointMap.put(gb.getGamerId(), p);
                }
            });
        });
        validGamerPointMap.forEach((id, p) -> {
            GamerPointService gamerPointService = SpringContextHolder.getBean(GamerPointService.class);
            gamerPointService.modifyGamerPoint(id, p);
        });
    }

    /**
     * 添加初始化帧
     */
    private void addInitFrame() {
        GamerPointService gamerPointService = SpringContextHolder.getBean(GamerPointService.class);
        RoomBean room = new RoomBean();
        room.setSeed(System.currentTimeMillis());
        room.setGamers(new ArrayList<>(3));
        for (int i = 0; i < gamers.size(); i ++) {
            GamerBean gb = new GamerBean();
            gb.setGamerId(gamers.get(i));
            gb.setOrder(i);
            gb.setPoint(gamerPointService.getPointByGamerId(gb.getGamerId()));
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
        if (scheduleTask != null) {
            scheduleTask.dispose();
        }
    }

    public boolean isOver() {
        return this.over;
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
