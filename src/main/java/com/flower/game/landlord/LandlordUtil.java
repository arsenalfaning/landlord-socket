package com.flower.game.landlord;

import com.flower.game.runtime.GameUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LandlordUtil {

    public static List<LandlordCard> convertCards(List<Byte> cards) {
        List<LandlordCard> cardList = cards.stream().map(e -> new LandlordCard(e)).collect(Collectors.toList());
        Collections.sort(cardList);
        return cardList;
    }

    /**
     * 查看是否满足出牌规则，如果满足则返回正常结果，否则返回空
     * @param cardList 必须先排好序
     * @return
     */
    public static LandlordCards checkCards(List<LandlordCard> cardList) {
        if (cardList.size() == 1) { //单张
            return constructCards(LandlordConst.Cards_Type_One, cardList, null);
        } else if (cardList.size() == 2) { //王炸或者对
            if (isAllValueEqual(cardList)) { //是对
                return constructCards(LandlordConst.Cards_Type_Two, cardList, null);
            } else if ( cardList.stream().allMatch(e -> e.getStyle().equals(GameUtil.Style_Joker)) ){ //是王炸
                return constructCards(LandlordConst.Cards_Type_Joker_Bomb, cardList, null);
            }
        } else if (cardList.size() == 3) { //三张
            return checkThree(cardList);
        } else if (cardList.size() == 4) { //四张
            if ( isAllValueEqual(cardList) ) { //炸弹
                return constructCards(LandlordConst.Cards_Type_Bomb, cardList, null);
            } else {
                return checkThree(cardList);
            }
        } else if (cardList.size() == 5) { //五张
            LandlordCards c = checkFour(cardList);
            if (c == null) {
                c = checkSeq(cardList);
            }
            return c;
        } else if (cardList.size() >= 6 && cardList.size() <= 8) {
            LandlordCards c = checkFour(cardList);
            if (c == null) {
                c = checkSeq(cardList);
            }
            if (c == null) {
                c = checkTwoSeq(cardList);
            }
            if (c == null) {
                c = checkThreeSeq(cardList);
            }
            return c;
        } else if (cardList.size() >= 9 && cardList.size() <= 12) {
            LandlordCards c = checkSeq(cardList);
            if (c == null) {
                c = checkTwoSeq(cardList);
            }
            if (c == null) {
                c = checkThreeSeq(cardList);
            }
            return c;
        } else if (cardList.size() >= 13 && cardList.size() <= 20) {
            LandlordCards c = checkTwoSeq(cardList);
            if (c == null) {
                c = checkThreeSeq(cardList);
            }
            return c;
        } else {
            return checkThreeSeq(cardList);
        }
        return null;
    }

    /**
     * 检测三张、三带一，三带二，牌数量3~5
     * @param src
     * @return
     */
    private static LandlordCards checkThree(List<LandlordCard> src) {
        List<LandlordCard> cardList = new ArrayList<>(src);
        if (cardList.size() == 3) {
            if ( isAllValueEqual(cardList) ) {
                return constructCards(LandlordConst.Cards_Type_Three, cardList, null);
            }
        } else if (cardList.size() <= 5){
            List<LandlordCard> c1 = findNValueEqual(cardList, 3);
            if (c1 != null) { //c1是主牌
                cardList.removeAll(c1);
                if (isAllValueEqual(cardList)) {
                    return constructCards(LandlordConst.Cards_Type_Three, c1, cardList);
                }
            }
        }
        return null;
    }

    /**
     * 检测四带1手，四带2手，牌数量5~8
     * @param src
     * @return
     */
    private static LandlordCards checkFour(List<LandlordCard> src) {
        List<LandlordCard> cardList = new ArrayList<>(src);
        if (cardList.size() >= 5 && cardList.size() <= 8) {
            List<LandlordCard> c1 = findNValueEqual(cardList, 4);
            if (c1 != null && c1.size() == 4) { //c1是主牌
                boolean b = cardList.removeAll(c1);
                if (cardList.size() <= 2) {
                    return constructCards(LandlordConst.Cards_Type_Four, c1, cardList);
                } else if (cardList.size() == 4) {
                    if (isAllValueEqual(cardList) || ( isAllValueEqual(cardList.subList(0, 2))  && isAllValueEqual(cardList.subList(2, 4)) )) {
                        return constructCards(LandlordConst.Cards_Type_Four, c1, cardList);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 检测顺子
     * @param src
     * @return
     */
    private static LandlordCards checkSeq(List<LandlordCard> src) {
        List<LandlordCard> cardList = new ArrayList<>(src);
        if (cardList.size() >= 5 && cardList.size() <= 12) {
            for (int i = 0; i <= cardList.size() - 2; i ++) {
                if ( !isTwoValueSeq(cardList.get(i), cardList.get(i + 1)) ) {
                    return null;
                }
            }
        }
        return constructCards(LandlordConst.Cards_Type_Seq, cardList, null);
    }

    /**
     * 检测连对
     * @param src
     * @return
     */
    private static LandlordCards checkTwoSeq(List<LandlordCard> src) {
        List<LandlordCard> cardList = new ArrayList<>(src);
        if (cardList.size() >= 6 && cardList.size() <= 20 && cardList.size() % 2 == 0) {
            for (int i = 0; i < cardList.size() - 1; i += 2) {
                if (!cardList.get(i).getValue().equals(cardList.get(i + 1).getValue())) {
                    return null;
                }
                if (i + 2 < cardList.size()) {
                    if (!isTwoValueSeq(cardList.get(i), cardList.get(i + 2))) {
                        return null;
                    }
                }
            }
            return constructCards(LandlordConst.Cards_Type_Two_Seq, cardList, null);
        } else {
            return null;
        }

    }

    /**
     * 检测飞机
     * @param src
     * @return
     */
    private static LandlordCards checkThreeSeq(List<LandlordCard> src) {
        List<LandlordCard> cardList = new ArrayList<>(src);
        if (cardList.size() >= 6 && cardList.size() <= 21) {
            List<List<LandlordCard>> list = new LinkedList<>();
            List<LandlordCard> main = new LinkedList<>();
            while (cardList.size() >= 3) {
                List<LandlordCard> cards = findNValueEqual(cardList, 3);
                if (cards == null) break;
                else {
                    cardList.removeAll(cards);
                    list.add(cards);
                    main.addAll(cards);
                }
            }
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size() - 1; i ++) {
                    if ( !isTwoValueSeq(list.get(i).get(0), list.get(i + 1).get(0)) ) {
                        return null;
                    }
                }
                if (cardList.size() == list.size() || cardList.isEmpty()) {
                    return constructCards(LandlordConst.Cards_Type_Three_Seq, main, cardList);
                }
                if (cardList.size() == list.size() * 2) {
                    for (int i = 0; i < cardList.size() - 1; i += 2) {
                        if (!cardList.get(i).getValue().equals(cardList.get(i + 1).getValue())) {
                            return null;
                        }
                    }
                    return constructCards(LandlordConst.Cards_Type_Three_Seq, main, cardList);
                }
            }
        }
        return null;
    }

    /**
     * 判断card1是否比card2大1
     * @param card1
     * @param card2
     * @return
     */
    private static boolean isTwoValueSeq(LandlordCard card1, LandlordCard card2) {
        return LandlordSortComparator.convertValue(card1.getValue()) -
                LandlordSortComparator.convertValue(card2.getValue()) == 1;
    }

    /**
     * 查找N张value相同的牌
     * @param cardList
     * @param n
     * @return
     */
    private static List<LandlordCard> findNValueEqual(List<LandlordCard> cardList, int n) {
        List<LandlordCard> copy = new ArrayList<>(cardList);
        for (int i = 0; i <= copy.size() - n; i ++) {
            if ( copy.get(i).getValue().equals( copy.get(i + n - 1).getValue() ) ) {
                return new ArrayList<>(cardList.subList(i, i + n));
            }
        }
        return null;
    }

    private static boolean isAllValueEqual(List<LandlordCard> cardList) {
        return cardList.stream().allMatch(e -> e.getValue().equals(cardList.get(0).getValue()));
    }

    private static LandlordCards constructCards(Byte type, List<LandlordCard> main, List<LandlordCard> append) {
        return new LandlordCards(type, main, append);
    }

}
