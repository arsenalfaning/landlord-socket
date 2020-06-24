package com.flower.game.landlord;

import lombok.Data;

import java.util.List;

@Data
public class LandlordCards implements Comparable<LandlordCards> {

    private Byte type;
    private List<Byte> main;
    private List<Byte> append;

    public LandlordCards(List<Byte> cards) {

    }
    @Override
    public int compareTo(LandlordCards another) {
        //1
        return 0;
    }
}
