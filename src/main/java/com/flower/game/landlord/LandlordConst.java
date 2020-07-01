package com.flower.game.landlord;

/**
 * <pre>
 * 出牌规则：
 * 1、单张，随便出，大小直接比较 1
 * 2、两张必须为对  2
 * 3、三张必须相同，可以带1手副牌 3~5
 * 4、四张必须相同，可以带1~2手副牌（要求两手副牌个数相同，比如都是对或者单）， 5~8
 * 5、5张及以上必须是顺子，个数相同的顺子可以比大小 5~11
 * 6、连对必须至少3对起 6~20且为偶数
 * 7、飞机的副牌个数必须相同 6~21
 * 8、炸弹可以比大小 4
 * 9、王炸最大 2
 * </pre>
 */
public class LandlordConst {

    public static final byte Cards_Type_One = 1; //单张牌
    public static final byte Cards_Type_Two = 2; //对
    public static final byte Cards_Type_Three = 3; //三张
    public static final byte Cards_Type_Four = 4; //四带一
    public static final byte Cards_Type_Seq = 5; //顺子
    public static final byte Cards_Type_Two_Seq = 6; //连对
    public static final byte Cards_Type_Three_Seq = 7; //飞机
    public static final byte Cards_Type_Bomb = 8; //炸弹
    public static final byte Cards_Type_Joker_Bomb = 9; //王炸


}
