package com.flower.game.landlord;

import com.flower.game.runtime.GameRuntime;
import com.flower.game.runtime.GamerRuntime;
import com.flower.game.runtime.PlayData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class LandlordVo {

    private List<LandlordGamerVo> others; //其他玩家牌信息
    private LandlordGamerVo my; //用户自己的牌信息，通常重新连接或者发完牌才拿到结果
    private String turnGamerId; //当前出牌者id
    private LocalDateTime turnDeadline; //出牌倒计时
    private PlayData lastTurn; //增量更新用的出牌结果
    private List<PlayData> roundHistory; //这一轮出牌历史
    private Byte status; //游戏状态

    public static LandlordVo fromGameRuntime(GameRuntime gameRuntime, String gamerId) {
        LandlordVo vo = new LandlordVo();
        vo.others = new ArrayList<>(2);
        for (GamerRuntime gr : gameRuntime.gamerRuntimeList) {
            if (gr.gamerId.equals(gamerId)) {
                vo.my = new LandlordGamerVo();
                vo.my.setCardsNumber(gr.cards == null ? null : gr.cards.size());
                vo.my.setGamerId(gr.gamerId);
                vo.my.setOrder(gr.order);
                vo.my.setCards(gr.cards);
            } else {
                LandlordGamerVo other = new LandlordGamerVo();
                other.setCardsNumber(gr.cards == null ? null : gr.cards.size());
                other.setGamerId(gr.gamerId);
                other.setOrder(gr.order);
                vo.others.add(other);
            }
        }
        vo.turnGamerId = gameRuntime.turnGamerId;
        vo.turnDeadline = gameRuntime.turnDeadline;
        vo.status = gameRuntime.status;

        return vo;
    }
}
