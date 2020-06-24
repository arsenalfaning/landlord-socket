package com.flower.game.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;

public class GameUtil {

    public static final Byte Heart_1 = (byte) 0x11; //红桃1
    public static final Byte Heart_2 = (byte) 0x12; //红桃2
    public static final Byte Heart_3 = (byte) 0x13; //红桃3
    public static final Byte Heart_4 = (byte) 0x14; //红桃4
    public static final Byte Heart_5 = (byte) 0x15; //红桃5
    public static final Byte Heart_6 = (byte) 0x16; //红桃6
    public static final Byte Heart_7 = (byte) 0x17; //红桃7
    public static final Byte Heart_8 = (byte) 0x18; //红桃8
    public static final Byte Heart_9 = (byte) 0x19; //红桃9
    public static final Byte Heart_10 = (byte) 0x1A; //红桃10
    public static final Byte Heart_J = (byte) 0x1B; //红桃J
    public static final Byte Heart_Q = (byte) 0x1C; //红桃Q
    public static final Byte Heart_K = (byte) 0x1D; //红桃K
    public static final Byte Spade_1 = (byte) 0x21; //黑桃1
    public static final Byte Spade_2 = (byte) 0x22; //黑桃2
    public static final Byte Spade_3 = (byte) 0x23; //黑桃3
    public static final Byte Spade_4 = (byte) 0x24; //黑桃4
    public static final Byte Spade_5 = (byte) 0x25; //黑桃5
    public static final Byte Spade_6 = (byte) 0x26; //黑桃6
    public static final Byte Spade_7 = (byte) 0x27; //黑桃7
    public static final Byte Spade_8 = (byte) 0x28; //黑桃8
    public static final Byte Spade_9 = (byte) 0x29; //黑桃9
    public static final Byte Spade_10 = (byte) 0x2A; //黑桃10
    public static final Byte Spade_J = (byte) 0x2B; //黑桃J
    public static final Byte Spade_Q = (byte) 0x2C; //黑桃Q
    public static final Byte Spade_K = (byte) 0x2D; //黑桃K
    public static final Byte Diamond_1 = (byte) 0x31; //方块1
    public static final Byte Diamond_2 = (byte) 0x32; //方块2
    public static final Byte Diamond_3 = (byte) 0x33; //方块3
    public static final Byte Diamond_4 = (byte) 0x34; //方块4
    public static final Byte Diamond_5 = (byte) 0x35; //方块5
    public static final Byte Diamond_6 = (byte) 0x36; //方块6
    public static final Byte Diamond_7 = (byte) 0x37; //方块7
    public static final Byte Diamond_8 = (byte) 0x38; //方块8
    public static final Byte Diamond_9 = (byte) 0x39; //方块9
    public static final Byte Diamond_10 = (byte) 0x3A; //方块10
    public static final Byte Diamond_J = (byte) 0x3B; //方块J
    public static final Byte Diamond_Q = (byte) 0x3C; //方块Q
    public static final Byte Diamond_K = (byte) 0x3D; //方块K
    public static final Byte Club_1 = (byte) 0x41; //梅花1
    public static final Byte Club_2 = (byte) 0x42; //梅花2
    public static final Byte Club_3 = (byte) 0x43; //梅花3
    public static final Byte Club_4 = (byte) 0x44; //梅花4
    public static final Byte Club_5 = (byte) 0x45; //梅花5
    public static final Byte Club_6 = (byte) 0x46; //梅花6
    public static final Byte Club_7 = (byte) 0x47; //梅花7
    public static final Byte Club_8 = (byte) 0x48; //梅花8
    public static final Byte Club_9 = (byte) 0x49; //梅花9
    public static final Byte Club_10 = (byte) 0x4A; //梅花10
    public static final Byte Club_J = (byte) 0x4B; //梅花J
    public static final Byte Club_Q = (byte) 0x4C; //梅花Q
    public static final Byte Club_K = (byte) 0x4D; //梅花K
    public static final Byte Joker_Kitten = (byte) 0x5E; //小猫
    public static final Byte Joker_Cat = (byte) 0x5F; //大猫

    /**
     * 原始一副牌
     * @return
     */
    public static List<Byte> allCards() {
        List<Byte> cards = new ArrayList<>(54);
        for (int i = 1; i <= 4; i ++) {
            for (int j = 1; j <= 13; j ++) {
                cards.add((byte) (i << 4 | j));
            }
        }
        cards.add(Joker_Kitten);
        cards.add(Joker_Cat);
        return cards;
    }

    /**
     * 进行洗牌
     * @param cards
     */
    public static void shuffleCards(List<Byte> cards) {
        Collections.shuffle(cards);
//        for (int i = 0; i < cards.length; i ++) {
//            int j = ThreadLocalRandom.current().nextInt(i, cards.length);
//            byte selected = cards[j];
//            cards[j] = cards[i];
//            cards[i] = selected;
//        }
    }

    public static final Byte Game_Status_Init = 0; //初始状态
    public static final Byte Game_Status_Playing = 10; //进行中
    public static final Byte Game_Status_Over = 127; //结束
}
