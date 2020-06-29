package com.flower.game.runtime;

import lombok.Data;

import java.util.List;

@Data
public class PlayData {
    private String gamerId;
    //出牌的数组
    private List<Byte> cards;
}
