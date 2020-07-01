package com.flower.game.runtime;

import java.util.List;
import java.util.Map;

public class GamerRuntime {

    //手中持有的牌
    public List<Byte> cards;

    //玩家唯一标识
    public String gamerId;

    //是否做好准备
    public Boolean ready;

    //出牌顺序
    public byte order;
//
//    //玩家其他属性
//    public Map<String, Object> data;
}
