package com.flower.game.game;

import com.flower.game.dto.GameFrame;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    /**
     * 版本号
     */
    private AtomicInteger version = new AtomicInteger(1);
    /**
     * 整局游戏的帧历史
     */
    private Map<Integer, GameFrame> frameHistory = new LinkedHashMap<>();
    /**
     * 待发送的动作
     */
    private List<Map> actionForSend = new LinkedList<>();

    /**
     * 接收动作
     * @param action
     */
    public synchronized void receiveAction(Map action) {
        this.actionForSend.add(action);
    }

    /**
     * 生成帧
     * @return
     */
    public synchronized GameFrame buildGameFrame() {
        GameFrame gameFrame = new GameFrame();
        gameFrame.setA(new ArrayList<>(actionForSend));
        gameFrame.setV(version.getAndAdd(1));
        gameFrame.setT(System.currentTimeMillis());
        this.frameHistory.put(gameFrame.getV(), gameFrame);
        this.actionForSend.clear();
        return gameFrame;
    }

    /**
     * 所有帧
     * @return
     */
    public List<GameFrame> allGameFrames() {
        return new ArrayList<>(this.frameHistory.values());
    }

    /**
     * 获取指定版本号的帧
     * @return
     */
    public GameFrame getGameFrameByVersion(Integer v) {
        return this.frameHistory.get(v);
    }
}
