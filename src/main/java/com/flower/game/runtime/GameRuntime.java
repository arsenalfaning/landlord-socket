package com.flower.game.runtime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class GameRuntime {

    //完整的一副牌
    public List<Byte> cards;

    //玩家数组
    public List<GamerRuntime> gamerRuntimeList;

    //当前需要出牌的玩家index
    public byte playOrder;

    //当前出牌倒计时
    public LocalDateTime playDeadline;

    //游戏状态
    public Byte status;

    //其他数据存储map
    public Map<String, Object>  dataMap;
}
