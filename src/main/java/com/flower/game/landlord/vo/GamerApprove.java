package com.flower.game.landlord.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 玩家抢地主申请
 */
@Data
@AllArgsConstructor
public class GamerApprove {
    private byte playOrder;//玩家的order
    private boolean value;//玩家是否抢
}
