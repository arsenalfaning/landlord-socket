package com.flower.game.landlord;

import java.util.Comparator;

public class LandlordSortComparator implements Comparator<Byte> {

    private static final int[] Value_Array = new int[] {0, 20, 21, 3, 4, 5, 6, 7, 8, 9, 10 ,11, 12, 13, 30, 40};
    private static final int[] Type_Array = new int[] {0, 2, 4, 3, 5, 1};

    @Override
    public int compare(Byte card1, Byte card2) {
        byte c1 = card1.byteValue();
        byte c2 = card2.byteValue();
        int value1 = c1 & 0x0f;
        int value2 = c2 & 0x0f;
        if (value1 != value2) {
            return Value_Array[value2] - Value_Array[value1];
        }
        int type1 = c1 & 0xf0;
        int type2 = c2 & 0xf0;
        return Type_Array[type1] - Type_Array[type2];
    }
}
