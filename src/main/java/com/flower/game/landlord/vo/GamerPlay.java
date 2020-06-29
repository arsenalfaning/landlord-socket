package com.flower.game.landlord.vo;

import com.flower.game.landlord.LandlordCards;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class GamerPlay {

    private byte playOrder;
    private LandlordCards landlordCards;
    private List<Byte> cards;
}
