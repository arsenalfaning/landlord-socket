package com.flower.game.landlord.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GamerResultVo {

    /**
     * 是否获胜
     */
    private Boolean win;
    /**
     * 积分变动
     */
    private BigDecimal delta;
    /**
     * 玩家
     */
    @JsonIgnore
    private String gamerId;
    /**
     * 玩家index
     */
    @JsonIgnore
    private byte order;
}
