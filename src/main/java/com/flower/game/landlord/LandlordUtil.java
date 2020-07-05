package com.flower.game.landlord;

import com.flower.game.runtime.GameUtil;

import java.util.*;
import java.util.stream.Collectors;

public class LandlordUtil {

    public static List<LandlordCard> convertCards(List<Byte> cards) {
        List<LandlordCard> cardList = cards.stream().map(e -> new LandlordCard(e)).collect(Collectors.toList());
        Collections.sort(cardList);
        return cardList;
    }

    /**
     * 提示出牌
     * @param cardList 出牌者手中所有牌
     * @param lastCards 上一手牌
     * @return 返回null表明
     */
    public static LandlordCards suggestCards(List<LandlordCard> cardList, LandlordCards lastCards) {
        if (lastCards != null) {
            switch (lastCards.getType()) {
                case LandlordConst.Cards_Type_Joker_Bomb:
                    return null;
                case LandlordConst.Cards_Type_Bomb:
                    List<LandlordCard> bomb = findNValueEqual(cardList, lastCards.getMainSize(), lastCards.getMainFirstCard());
                    if (bomb != null) {
                        return constructCards(LandlordConst.Cards_Type_Bomb, bomb, null);
                    }
                    //尝试找王炸
                    return suggestJokerBomb(cardList);
                case LandlordConst.Cards_Type_One:
                case LandlordConst.Cards_Type_Two:
                case LandlordConst.Cards_Type_Three:
                case LandlordConst.Cards_Type_Three_Append_One:
                case LandlordConst.Cards_Type_Four_Append_One:
                case LandlordConst.Cards_Type_Four_Append_Two:
                    List<LandlordCard> one = findNValueEqual(cardList, lastCards.getMainSize(), lastCards.getMainFirstCard());
                    if (one != null) {
                        List<LandlordCard> append = null;
                        if ( lastCards.getAppendSize() > 0 ) {
                            List<LandlordCard> some = new ArrayList<>(cardList);
                            some.removeAll(one);
                            append = suggestNOne(some, lastCards.getAppendSize());
                        }
                        return constructCards(lastCards.getType(), one, append);
                    }
                    //尝试找炸弹
                    return suggestBombOrJokerBomb(cardList);
                case LandlordConst.Cards_Type_Three_Append_Double:
                case LandlordConst.Cards_Type_Four_Append_Double:
                case LandlordConst.Cards_Type_Four_Append_Two_Double:
                    List<LandlordCard> one1 = findNValueEqual(cardList, lastCards.getMainSize(), lastCards.getMainFirstCard());
                    if (one1 != null) {
                        List<LandlordCard> append = null;
                        if ( lastCards.getAppendSize() > 0 ) {
                            List<LandlordCard> some = new ArrayList<>(cardList);
                            some.removeAll(one1);
                            append = suggestNTwo(some, lastCards.getAppendSize() / 2);
                        }
                        return constructCards(lastCards.getType(), one1, append);
                    }
                    return suggestBombOrJokerBomb(cardList);
                case LandlordConst.Cards_Type_Seq:
                    List<LandlordCard> seq = suggestSeq(cardList, lastCards.getMainFirstCard(), lastCards.getMainSize(), 1);
                    if (seq != null) {
                        return constructCards(lastCards.getType(), seq, null);
                    }
                    return suggestBombOrJokerBomb(cardList);
                case LandlordConst.Cards_Type_Two_Seq:
                    List<LandlordCard> seq2 = suggestSeq(cardList, lastCards.getMainFirstCard(), lastCards.getMainSize() / 2, 2);
                    if (seq2 != null) {
                        return constructCards(lastCards.getType(), seq2, null);
                    }
                    return suggestBombOrJokerBomb(cardList);
                case LandlordConst.Cards_Type_Three_Seq:
                case LandlordConst.Cards_Type_Three_Seq_Append_One:
                case LandlordConst.Cards_Type_Three_Seq_Append_Double:
                    List<LandlordCard> seq3 = suggestSeq(cardList, lastCards.getMainFirstCard(), lastCards.getMainSize() / 3, 3);
                    if (seq3 != null) {
                        List<LandlordCard> rest = new ArrayList<>(cardList);
                        rest.removeAll(seq3);
                        if ( lastCards.getAppendSize() > 0 ) {
                            List<LandlordCard> append;
                            if ( lastCards.getType().equals(LandlordConst.Cards_Type_Three_Seq_Append_Double) ) {
                                append = suggestNTwo(rest, lastCards.getMainSize() / 3);
                            } else {
                                append = suggestNOne(rest, lastCards.getMainSize() / 3);
                            }
                            if ( append != null ) {
                                return constructCards(lastCards.getType(), seq3, append);
                            }
                        } else {
                            return constructCards(lastCards.getType(), seq3, null);
                        }

                    }
                    return suggestBombOrJokerBomb(cardList);
            }
        } else {
            LandlordCards r = checkCards(cardList);
            if (r == null) {
                return suggestMin(cardList);
            }
            return r;
        }
        return null;
    }

    /**
     * 建议发牌
     * @param cardList
     * @return
     */
    private static LandlordCards suggestMin(List<LandlordCard> cardList) {
        int times = 0;
        LandlordCard min = cardList.get(cardList.size() - 1);
        for (int i = cardList.size() - 1; i >= 0; i --) {
            if ( cardList.get(i).getValue().equals(min.getValue()) ) {
                times ++;
            } else {
                break;
            }
        }
        if (times == 1) {
            return constructCards(LandlordConst.Cards_Type_One, Arrays.asList(min), null);
        } else if (times == 2) {
            return constructCards(LandlordConst.Cards_Type_Two, cardList.subList(cardList.size() - 2, cardList.size()), null);
        } else if (times == 3) {
            return constructCards(LandlordConst.Cards_Type_Three, cardList.subList(cardList.size() - 3, cardList.size()), null);
        } else {
            return constructCards(LandlordConst.Cards_Type_Bomb, cardList.subList(cardList.size() - 4, cardList.size()), null);
        }
    }

    /**
     * 查找顺子
     * @param cardList
     * @param maxCard
     * @param length
     * @param size 1表示单连，2表示连对，3表示飞机
     * @return
     */
    private static List<LandlordCard> suggestSeq(List<LandlordCard> cardList, LandlordCard maxCard, int length, int size) {
        if (cardList.size() < length * size) return null;
        int max = LandlordSortComparator.nextSeqValue( LandlordSortComparator.convertValue(maxCard.getValue()) );
        if (max == 0) return null;
        List<Integer>[] indexArray = new List[41];
        for (int i = 0; i < cardList.size(); i ++) {
            LandlordCard card = cardList.get(i);
            int v = LandlordSortComparator.convertValue(card.getValue());
            List<Integer> indexList = indexArray[v];
            if (indexList == null) {
                indexList = new LinkedList<>();
                indexArray[v] = indexList;
            }
            indexList.add(i);
        }
        while (max > 0) {
            int existValue = max + 1;
            boolean exist = true;
            List<LandlordCard> result = new ArrayList<>(length * size);
            for (int i = max; i >= max - length + 1; i --) {
                if (indexArray[i] != null && indexArray[i].size() >= size) {
                    existValue = i;
                    for (int j = 0; j < size; j ++) {
                        result.add(cardList.get(indexArray[i].get(j)));
                    }
                } else {
                    exist = false;
                    break;
                }
            }
            if (exist) {
                return result;
            }
            max = LandlordSortComparator.nextSeqValue(existValue + length - 2);
        }
        return null;
    }

    /**
     * 查找N张对
     * @param cardList
     * @return
     */
    private static List<LandlordCard> suggestNTwo(List<LandlordCard> cardList, int n) {
        if (cardList.size() >= 2 * n) {
            List<Integer> indexList = new ArrayList<>(2 * n);
            for (int i = cardList.size() - 1; i >= 1; i --) {
                if (cardList.get(i).getValue().equals(cardList.get(i - 1).getValue())) {
                    indexList.add(i);
                    indexList.add(i - 1);
                    i --;
                    if (indexList.size() == 2 * n) break;
                }
            }
            if (indexList.size() == 2 * n) {
                List<LandlordCard> r = new ArrayList<>(2 * n);
                for (int index : indexList) {
                    r.add(cardList.get(index));
                }
                return r;
            }
        }
        return null;
    }

    /**
     * 查找N张单
     * @param cardList
     * @return
     */
    private static List<LandlordCard> suggestNOne(List<LandlordCard> cardList, int n) {
        if (cardList.size() >= n) {
            return new ArrayList<>(cardList.subList(cardList.size() - n, cardList.size()));
        }
        return null;
    }

    /**
     * 查找炸弹或者王炸
     * @param cardList
     * @return
     */
    private static LandlordCards suggestBombOrJokerBomb(List<LandlordCard> cardList) {
        List<LandlordCard> bomb = findNValueEqualFromRight(cardList, 4);
        if (bomb != null) {
            return constructCards(LandlordConst.Cards_Type_Bomb, bomb, null);
        }
        return suggestJokerBomb(cardList);
    }

    /**
     * 查找王炸
     * @param cardList
     * @return
     */
    private static LandlordCards suggestJokerBomb(List<LandlordCard> cardList) {
        if (cardList.size() >= 2) {
            List<LandlordCard> some = cardList.subList(0, 2);
            if (isJokerBomb(some)) {
                return constructCards(LandlordConst.Cards_Type_Joker_Bomb, some, null);
            }
        }
        return null;
    }

    private static boolean isJokerBomb(List<LandlordCard> cardList) {
        return cardList.stream().allMatch(e -> e.getStyle().equals(GameUtil.Style_Joker));
    }

    /**
     * 检查出牌是否满足特定类型
     * @param cardList
     * @param cards
     * @return
     */
    public static LandlordCards checkCardsForType(List<LandlordCard> cardList, LandlordCards cards) {
        LandlordCards r = suggestCards(cardList, cards);
        if  (r != null && r.getMainSize() + r.getAppendSize() == cardList.size()) {
            return r;
        }
        return null;
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
            } else if ( isJokerBomb(cardList) ){ //是王炸
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
                c = checkThree(cardList);
            }
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
                    if (cardList.size() == 1) {
                        return constructCards(LandlordConst.Cards_Type_Three_Append_One, c1, cardList);
                    } else {
                        return constructCards(LandlordConst.Cards_Type_Three_Append_Double, c1, cardList);
                    }

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
                    if (cardList.size() == 1) {
                         return constructCards(LandlordConst.Cards_Type_Four_Append_One, c1, cardList);
                    } else {
                        if (isAllValueEqual(cardList)) {
                            return constructCards(LandlordConst.Cards_Type_Four_Append_Double, c1, cardList);
                        } else {
                            return constructCards(LandlordConst.Cards_Type_Four_Append_Two, c1, cardList);
                        }
                    }
                } else if (cardList.size() == 4) {
                    if (isAllValueEqual(cardList) || ( isAllValueEqual(cardList.subList(0, 2))  && isAllValueEqual(cardList.subList(2, 4)) )) {
                        return constructCards(LandlordConst.Cards_Type_Four_Append_Two_Double, c1, cardList);
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
                if (cardList.isEmpty()) {
                    return constructCards(LandlordConst.Cards_Type_Three_Seq, main, cardList);
                }
                if (cardList.size() == list.size()) {
                    return constructCards(LandlordConst.Cards_Type_Three_Seq_Append_One, main, cardList);
                }
                if (cardList.size() == list.size() * 2) {
                    for (int i = 0; i < cardList.size() - 1; i += 2) {
                        if (!cardList.get(i).getValue().equals(cardList.get(i + 1).getValue())) {
                            return null;
                        }
                    }
                    return constructCards(LandlordConst.Cards_Type_Three_Seq_Append_Double, main, cardList);
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

    /**
     * 查找N张value相同的牌，要求比给定的牌大
     * @param cardList
     * @param n
     * @return
     */
    private static List<LandlordCard> findNValueEqual(List<LandlordCard> cardList, int n, LandlordCard targetCard) {
        List<LandlordCard> copy = new ArrayList<>(cardList);
        int targetValue = LandlordSortComparator.convertValue(targetCard.getValue());
        for (int i = copy.size() - 1; i >= n - 1; i --) {
            if (LandlordSortComparator.convertValue(copy.get(i).getValue()) > targetValue &&  copy.get(i).getValue().equals( copy.get(i - n + 1).getValue() ) ) {
                return new ArrayList<>(cardList.subList(i - n + 1, i + 1));
            }
        }
        return null;
    }

    /**
     * 查找N张value相同的牌，从小到大找
     * @param cardList
     * @param n
     * @return
     */
    private static List<LandlordCard> findNValueEqualFromRight(List<LandlordCard> cardList, int n) {
        if (n <= 0) return new ArrayList<>();
        List<LandlordCard> copy = new ArrayList<>(cardList);
        for (int i = copy.size() - 1; i >= n - 1; i --) {
            if (copy.get(i).getValue().equals( copy.get(i - n + 1).getValue() ) ) {
                return new ArrayList<>(cardList.subList(i - n + 1, i + 1));
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
