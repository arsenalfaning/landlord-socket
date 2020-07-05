package com.flower.game.landlord;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 一手牌
 */
public class LandlordCards implements Comparable<LandlordCards> {

    /**
     * 牌类型
     * @see LandlordConst
     */
    private Byte type;
    private List<LandlordCard> main;//主牌
    private List<LandlordCard> append; //副牌

    public LandlordCards(Byte type, List<LandlordCard> main, List<LandlordCard> append) {
        this.type = type;
        this.main = main;
        this.append = append;
    }

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
        if (another.type.equals(LandlordConst.Cards_Type_Joker_Bomb)) {
            return -1;
        }
        if (type.equals(LandlordConst.Cards_Type_Bomb) && !another.type.equals(LandlordConst.Cards_Type_Bomb)) {
            return 1;
        }
        if (!type.equals(LandlordConst.Cards_Type_Bomb) && another.type.equals(LandlordConst.Cards_Type_Bomb)) {
            return -1;
        }
        if (type.equals(another.type) && main.size() == another.main.size() && intEqualNullable(append, another.append)) { //如果牌类型相同
            return -main.get(0).compareTo(another.main.get(0));
        }
        return 0;
    }

    private boolean intEqualNullable(Collection<LandlordCard> c1, Collection<LandlordCard> c2) {
        int i1 = c1 == null ? 0 : c1.size();
        int i2 = c2 == null ? 0 : c2.size();
        return i1 == i2;
    }

    public Byte getType() {
        return this.type;
    }

    public int getMainSize() {
        return this.main.size();
    }

    public LandlordCard getMainFirstCard() {
        return main.get(0);
    }

    public int getAppendSize() {
        if (this.append == null) return 0;
        return this.append.size();
    }
    public List<Byte> toCards() {
        List<Byte> bytes = new LinkedList<>();
        main.stream().forEach(e -> bytes.add(e.getCard()));
        if (append != null) {
            append.stream().forEach(e -> bytes.add(e.getCard()));
        }
        return bytes;
    }
}
