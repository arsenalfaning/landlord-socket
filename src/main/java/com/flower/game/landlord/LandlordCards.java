package com.flower.game.landlord;

import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * 一手牌
 */
@AllArgsConstructor
public class LandlordCards implements Comparable<LandlordCards> {

    private Byte type;
    private List<LandlordCard> main;//主牌
    private List<LandlordCard> append; //副牌

    /**
     * 返回0不一定表示一样大，也可能无法比较，比如单张5和一对3
     * @param another
     * @return
     */
    @Override
    public int compareTo(LandlordCards another) {
        //1.自己是王炸，返回1
        if (type.equals(LandlordConst.Cards_Type_Joker_Bomb)) {
            return 1;
        }
        if (type.equals(another.type) && main.size() == another.main.size() && intEqualNullable(append, another.append)) { //如果牌类型相同
            return - main.get(0).compareTo(another.main.get(0));
        }
        return 0;
    }

    private boolean intEqualNullable(Collection<LandlordCard> c1, Collection<LandlordCard> c2) {
        int i1 = c1 == null ? 0 : c1.size();
        int i2 = c2 == null ? 0 : c2.size();
        return i1 == i2;
    }
}
