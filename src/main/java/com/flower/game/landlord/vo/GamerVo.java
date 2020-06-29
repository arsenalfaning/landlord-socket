package com.flower.game.landlord.vo;

import lombok.Data;

import java.util.List;

@Data
public class GamerVo {
    private Boolean ready; //是否做好准备，只有开始游戏前有效
    private List<Byte> cards;//牌，明牌时有值，除非是自己的牌
    private Integer cardsNumber;//牌数量，一直有效
}
