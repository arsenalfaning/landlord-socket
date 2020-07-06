package com.flower.game.landlord.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GameVo {

    private Byte status;//游戏状态
    private GamerVo myself;//自己
    private GamerVo prev;//上家
    private GamerVo next;//下家
    private String playingGamer;//当前需要出牌的玩家-myself,prev,next
    private LocalDateTime playingDeadline;//出牌截止时间
    private GamerPlayVo playing;//当前出牌
    private List<GamerPlayVo> playHistory;//当回合出牌历史
    private List<GamerApproveVo> approveHistory;//抢地主历史
    private GamerApproveVo approve;//当前抢地主动作
    private List<Byte> landlordRest;//地主余牌
    private ResultVo result;//结果
}
