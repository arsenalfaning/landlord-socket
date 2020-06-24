package com.flower.game.landlord;

import lombok.Data;

import java.util.List;

@Data
public class LandlordGamerVo {

    private List<Byte> cards;
    private Integer cardsNumber;
    private String gamerId;
    private byte order;
}
