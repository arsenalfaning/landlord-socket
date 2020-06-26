package com.flower.game.landlord;

import com.flower.game.runtime.GameUtil;

/**
 * 一张牌
 */
public class LandlordCard implements Comparable<LandlordCard>{

    /**
     * 原始牌的byte值
     * @see GameUtil
     */
    private Byte card;
    private Byte style;//花色，比如红桃
    private Byte value;//值，比如J为11

    public LandlordCard(Byte card) {
        this.card = card;
        this.style = (byte) ( (card.byteValue() & 0xf0) >> 4 );
        this.value = (byte) (card.byteValue() & 0x0f);
    }

    public Byte getCard() {
        return card;
    }
    public Byte getStyle() {
        return style;
    }
    public Byte getValue() {
        return value;
    }

    @Override
    public int compareTo(LandlordCard another) {
        return LandlordSortComparator.compareValue(value, another.value);
    }

    @Override
    public String toString() {
        return "LandlordCard{" +
                "card=" + card +
                ", style=" + style +
                ", value=" + value +
                '}';
    }
}
