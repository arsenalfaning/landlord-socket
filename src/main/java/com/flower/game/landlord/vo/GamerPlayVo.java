package com.flower.game.landlord.vo;

import lombok.Data;

import java.util.List;

@Data
public class GamerPlayVo {
    private String gamer;
    private List<Byte> cards;
    private Byte type;
}
