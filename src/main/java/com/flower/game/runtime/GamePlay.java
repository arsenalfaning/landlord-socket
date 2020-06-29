package com.flower.game.runtime;

import java.util.List;

public interface GamePlay {

    /**
     * 初始化
     */
    void init();

    /**
     * 加入游戏
     * @param gamerId
     * @return
     */
    boolean join(String gamerId);

    /**
     * 做好准备
     * @return
     */
    boolean ready(String gamerId);

    /**
     * 取消准备
     * @return
     */
    boolean unReady(String gamerId);

    /**
     * 洗牌
     */
    boolean shuffle();

    /**
     * 切牌
     */
    boolean cut();

    /**
     * 发牌
     */
    boolean deal();

    /**
     * 理牌
     * @return
     */
    boolean sort();

    /**
     * 切换出牌方
     */
    void turn();

    /**
     * 出牌
     */
    boolean play(List<Byte> cards, String gamerId);

    /**
     * 结束
     * @return
     */
    boolean complete();
}
