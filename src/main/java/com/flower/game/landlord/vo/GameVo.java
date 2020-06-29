package com.flower.game.landlord.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameVo {

    private Byte status;//游戏状态
    private GamerVo myself;//自己
    private GamerVo prev;//上家
    private GamerVo next;//下家
    private String playingGamer;//当前需要出牌的玩家-myself,prev,next
    private LocalDateTime playingDeadline;//出牌截止时间

}
