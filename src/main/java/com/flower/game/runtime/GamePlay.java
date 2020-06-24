package com.flower.game.runtime;

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
    GameRuntime join(String gamerId);

    /**
     * 做好准备
     * @return
     */
    GameRuntime ready();

    /**
     * 取消准备
     * @return
     */
    GameRuntime unReady();

    /**
     * 洗牌
     */
    GameRuntime shuffle();

    /**
     * 切牌
     */
    GameRuntime cut();

    /**
     * 发牌
     */
    GameRuntime deal();

    /**
     * 理牌
     * @return
     */
    GameRuntime sort();

    /**
     * 出牌
     */
    GameRuntime play(PlayData playData);

    /**
     * 结束
     * @return
     */
    GameRuntime complete();
}
