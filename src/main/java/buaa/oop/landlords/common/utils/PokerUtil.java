package buaa.oop.landlords.common.utils;

import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.entities.PokerSell;
import buaa.oop.landlords.common.enums.PokerLevel;
import buaa.oop.landlords.common.enums.PokerType;
import buaa.oop.landlords.common.enums.SellType;

import java.util.*;

public class PokerUtil {
    private static final List<Poker> basePokers = new ArrayList<>(54);

    private static final Comparator<Poker> pokerComparator = (o1, o2) -> o1.getLevel().getLevel() - o2.getLevel().getLevel();

    static {
        PokerLevel[] pokerLevels = PokerLevel.values();
        PokerType[] pokerTypes = PokerType.values();
        for (PokerLevel level : pokerLevels) {
            if (level == PokerLevel.LEVEL_SMALL_KING) {
                basePokers.add(new Poker(level, PokerType.BLANK));
                continue;
            }
            if (level == PokerLevel.LEVEL_BIG_KING) {
                basePokers.add(new Poker(level, PokerType.BLANK));
                continue;
            }
            for (PokerType type : pokerTypes) {
                if (type == PokerType.BLANK) {
                    continue;
                }
                basePokers.add(new Poker(level, type));
            }
        }
    }
    /**
     * 发牌
     * @return 三个玩家的牌 3*17+3
     */
    public static List<List<Poker>> distributePokers() {
        Collections.shuffle(basePokers);
        List<List<Poker>> pokersList = new ArrayList<List<Poker>>();
        List<Poker> pokers1 = new ArrayList<>(17);
        pokers1.addAll(basePokers.subList(0, 17));
        List<Poker> pokers2 = new ArrayList<>(17);
        pokers2.addAll(basePokers.subList(17, 34));
        List<Poker> pokers3 = new ArrayList<>(17);
        pokers3.addAll(basePokers.subList(34, 51));
        List<Poker> pokers4 = new ArrayList<>(3);
        pokers4.addAll(basePokers.subList(51, 54));
        pokersList.add(pokers1);
        pokersList.add(pokers2);
        pokersList.add(pokers3);
        pokersList.add(pokers4);
        for (List<Poker> pokers : pokersList) {
            sortPokers(pokers);
        }
        return pokersList;
    }

    /**
     * 排序
     * @param pokers 牌
     */
    public static void sortPokers(List<Poker> pokers) {pokers.sort(pokerComparator);}

    /**
     * 检查牌型是否合法/判断牌型
     * @param pokers 牌组
     */
    public static PokerSell checkPokerSell(List<Poker> pokers) {
        if (pokers == null || pokers.isEmpty()) {
            return new PokerSell(null, SellType.ILLEGAL,  -1);
        }
        sortPokers(pokers);

        int[] levelTable = new int[20];
        for (Poker poker : pokers) {
            levelTable[poker.getLevel().getLevel()]++;
        }

        int startIndex = -1;
        int endIndex = -1;
        int count = 0;

        int singleCount = 0;
        int doubleCount = 0;
        int threeCount = 0;
        int threeStartIndex = -1;
        int threeEndIndex = -1;
        int fourCount = 0;
        int fourStartIndex = -1;
        int fourEndIndex = -1;
        for (int index = 3; index < 18; index++) {
            int value = levelTable[index];
            if (value == 0) {
                continue;
            }
            endIndex = index;
            count++;
            if (startIndex == -1) {
                startIndex = index;
            }
            if (value == 1) {
                singleCount++;
            } else if (value == 2) {
                doubleCount++;
            } else if (value == 3) {
                if (threeStartIndex == -1) {
                    threeStartIndex = index;
                }
                threeEndIndex = index;
                threeCount++;
            } else if (value == 4) {
                if (fourStartIndex == -1) {
                    fourStartIndex = index;
                }
                fourEndIndex = index;
                fourCount++;
            }
        }
        if(startIndex == PokerLevel.LEVEL_SMALL_KING.getLevel() && endIndex == PokerLevel.LEVEL_BIG_KING.getLevel() && singleCount == 2){
            return new PokerSell(pokers, SellType.KING_BOMB, endIndex);
        }

        if(singleCount + doubleCount + threeCount == 0 && fourCount == 1){
            return new PokerSell(pokers, SellType.BOMB, endIndex);
        }
        if (startIndex == endIndex) {
            if (levelTable[startIndex] == 1) {
                return new PokerSell(pokers, SellType.SINGLE,  endIndex);
            } else if (levelTable[startIndex] == 2) {
                return new PokerSell(pokers, SellType.DOUBLE, endIndex);
            } else if (levelTable[startIndex] == 3) {
                return new PokerSell(pokers, SellType.THREE, endIndex);
            }
        }

        if (endIndex - startIndex == count - 1 && endIndex < PokerLevel.LEVEL_2.getLevel()) {
            if (levelTable[startIndex] == 1 && singleCount >= 5 && doubleCount + threeCount + fourCount == 0) {
                return new PokerSell(pokers, SellType.SINGLE_STRAIGHT, endIndex);
            } else if (levelTable[startIndex] == 2 && doubleCount > 2 && singleCount + threeCount + fourCount == 0) {
                return new PokerSell(pokers, SellType.DOUBLE_STRAIGHT, endIndex);
            } else if (levelTable[startIndex] == 3 && threeCount > 1 && doubleCount + singleCount + fourCount == 0) {
                return new PokerSell(pokers, SellType.THREE_STRAIGHT, endIndex);
            } else if (levelTable[startIndex] == 4 && fourCount > 1 && doubleCount + threeCount + singleCount == 0) {
                return new PokerSell(pokers, SellType.FOUR_STRAIGHT, endIndex);
            }
        }

        if (threeCount != 0) {
            if (singleCount != 0 && singleCount == threeCount && doubleCount == 0 && fourCount == 0) {
                if (threeCount == 1) {
                    return new PokerSell(pokers, SellType.THREE_ZONES_SINGLE, endIndex);
                }
                if (threeEndIndex - threeStartIndex + 1 == threeCount && threeEndIndex < PokerLevel.LEVEL_2.getLevel()) {
                    return new PokerSell(pokers, SellType.THREE_STRAIGHT_WITH_SINGLE, endIndex);
                }
            } else if (doubleCount != 0 && doubleCount == threeCount && singleCount == 0 && fourCount == 0) {
                if (threeCount == 1) {
                    return new PokerSell(pokers, SellType.THREE_ZONES_DOUBLE, endIndex);
                }
                if (threeEndIndex - threeStartIndex + 1 == threeCount && threeEndIndex < PokerLevel.LEVEL_2.getLevel()) {
                    return new PokerSell(pokers, SellType.THREE_STRAIGHT_WITH_DOUBLE, endIndex);
                }
            } else if (singleCount + doubleCount * 2 == threeCount && fourCount == 0) {
                return new PokerSell(pokers, SellType.THREE_STRAIGHT_WITH_SINGLE, endIndex);
            }
        }

        if (fourCount != 0) {
            if (singleCount != 0 && singleCount == fourCount * 2 && doubleCount == 0 && threeCount == 0) {
                if (fourCount == 1) {
                    return new PokerSell(pokers, SellType.FOUR_ZONES_SINGLE, endIndex);
                }
                if (fourEndIndex - fourStartIndex + 1 == fourCount && fourEndIndex < PokerLevel.LEVEL_2.getLevel()) {
                    return new PokerSell(pokers, SellType.FOUR_STRAIGHT_WITH_SINGLE, endIndex);
                }
            } else if (doubleCount != 0 && doubleCount == fourCount * 2 && singleCount == 0 && threeCount == 0) {
                if (fourCount == 1) {
                    return new PokerSell(pokers, SellType.FOUR_ZONES_DOUBLE, endIndex);
                }
                if (fourEndIndex - fourStartIndex + 1 == fourCount && fourEndIndex < PokerLevel.LEVEL_2.getLevel()) {
                    return new PokerSell(pokers, SellType.FOUR_STRAIGHT_WITH_DOUBLE, endIndex);
                }
            }
        }
        return new PokerSell(null, SellType.ILLEGAL, -1);
    }

    /**
     * 检查牌型是否合法/判断牌型
     * @param pokers 牌组
     */
    public static List<PokerSell> parsePokerSells(List<Poker> pokers) {
        List<PokerSell> pokerSells = new ArrayList<>();
        int size = pokers.size();


        {
            int count = 0;
            int lastLevel = -1;
            List<Poker> sellPokers = new ArrayList<>(4);
            for (Poker poker : pokers) {
                int level = poker.getLevel().getLevel();
                if (lastLevel == -1) {
                    ++count;
                } else {
                    if (level == lastLevel) {
                        ++count;
                    } else {
                        count = 1;
                        sellPokers.clear();
                    }
                }
                sellPokers.add(poker);
                if (count == 1) {
                    pokerSells.add(new PokerSell(ListUtil.getList(sellPokers), SellType.SINGLE, poker.getLevel().getLevel()));
                } else if (count == 2) {
                    pokerSells.add(new PokerSell(ListUtil.getList(sellPokers), SellType.DOUBLE, poker.getLevel().getLevel()));
                } else if (count == 3) {
                    pokerSells.add(new PokerSell(ListUtil.getList(sellPokers), SellType.THREE, poker.getLevel().getLevel()));
                } else if (count == 4) {
                    pokerSells.add(new PokerSell(ListUtil.getList(sellPokers), SellType.BOMB, poker.getLevel().getLevel()));
                }

                lastLevel = level;
            }
        }

        {
            parsePokerSellStraight(pokerSells, SellType.SINGLE);
            parsePokerSellStraight(pokerSells, SellType.DOUBLE);
            parsePokerSellStraight(pokerSells, SellType.THREE);
            parsePokerSellStraight(pokerSells, SellType.BOMB);
        }

        {
            for (int index = 0; index < pokerSells.size(); index++) {
                PokerSell sell = pokerSells.get(index);
                if (sell.getSellType() == SellType.THREE) {
                    parseArgs(pokerSells, sell, 1, SellType.SINGLE, SellType.THREE_ZONES_SINGLE);
                    parseArgs(pokerSells, sell, 1, SellType.DOUBLE, SellType.THREE_ZONES_DOUBLE);
                } else if (sell.getSellType() == SellType.BOMB) {
                    parseArgs(pokerSells, sell, 2, SellType.SINGLE, SellType.FOUR_ZONES_SINGLE);
                    parseArgs(pokerSells, sell, 2, SellType.DOUBLE, SellType.FOUR_ZONES_DOUBLE);
                } else if (sell.getSellType() == SellType.THREE_STRAIGHT) {
                    int count = sell.getSellPokers().size() / 3;
                    parseArgs(pokerSells, sell, count, SellType.SINGLE, SellType.THREE_STRAIGHT_WITH_SINGLE);
                    parseArgs(pokerSells, sell, count, SellType.DOUBLE, SellType.THREE_STRAIGHT_WITH_DOUBLE);
                } else if (sell.getSellType() == SellType.FOUR_STRAIGHT) {
                    int count = (sell.getSellPokers().size() / 4) * 2;
                    parseArgs(pokerSells, sell, count, SellType.SINGLE, SellType.FOUR_STRAIGHT_WITH_SINGLE);
                    parseArgs(pokerSells, sell, count, SellType.DOUBLE, SellType.FOUR_STRAIGHT_WITH_DOUBLE);
                }
            }
        }

        {
            if (size > 1) {
                if (pokers.get(size - 1).getLevel() == PokerLevel.LEVEL_BIG_KING && pokers.get(size - 2).getLevel() == PokerLevel.LEVEL_SMALL_KING) {
                    pokerSells.add(new PokerSell(ListUtil.getList(new Poker[]{pokers.get(size - 2), pokers.get(size - 1)}), SellType.KING_BOMB, PokerLevel.LEVEL_BIG_KING.getLevel()));
                }
            }
        }

        return pokerSells;
    }

    private static void parsePokerSellStraight(List<PokerSell> pokerSells, SellType sellType) {
        int minLength = -1;
        int width = -1;
        SellType targetSellType = null;
        if (sellType == SellType.SINGLE) {
            minLength = 5;
            width = 1;
            targetSellType = SellType.SINGLE_STRAIGHT;
        } else if (sellType == SellType.DOUBLE) {
            minLength = 3;
            width = 2;
            targetSellType = SellType.DOUBLE_STRAIGHT;
        } else if (sellType == SellType.THREE) {
            minLength = 2;
            width = 3;
            targetSellType = SellType.THREE_STRAIGHT;
        } else if (sellType == SellType.BOMB) {
            minLength = 2;
            width = 4;
            targetSellType = SellType.FOUR_STRAIGHT;
        }

        int increase = 0;
        int lastLevel = -1;
        List<Poker> sellPokers = new ArrayList<>(4);
        for (int index = 0; index < pokerSells.size(); index++) {
            PokerSell sell = pokerSells.get(index);

            if (sell.getSellType() != sellType) {
                continue;
            }
            int level = sell.getEndIndex();
            if (lastLevel == -1) {
                increase++;
            } else {
                if (level - 1 == lastLevel && level != PokerLevel.LEVEL_2.getLevel()) {
                    ++increase;
                } else {
                    addPokers(pokerSells, minLength, width, targetSellType, increase, sellPokers);

                    increase = 1;
                }
            }
            sellPokers.addAll(sell.getSellPokers());
            lastLevel = level;
        }
        addPokers(pokerSells, minLength, width, targetSellType, increase, sellPokers);
    }

    private static void addPokers(List<PokerSell> pokerSells, int minLength, int width, SellType targetSellType, int increase, List<Poker> sellPokers) {
        if (increase >= minLength) {
            for (int s = 0; s <= increase - minLength; s++) {
                int len = minLength + s;
                for (int subIndex = 0; subIndex <= increase - len; subIndex++) {
                    List<Poker> pokers = ListUtil.getList(sellPokers.subList(subIndex * width, (subIndex + len) * width));
                    pokerSells.add(new PokerSell(pokers, targetSellType, pokers.get(pokers.size() - 1).getLevel().getLevel()));
                }
            }
        }
        sellPokers.clear();
    }

    private static void parseArgs(List<PokerSell> pokerSells, PokerSell pokerSell, int deep, SellType sellType, SellType targetSellType) {
        Set<Integer> existLevelSet = new HashSet<>();
        for (Poker p : pokerSell.getSellPokers()) {
            existLevelSet.add(p.getLevel().getLevel());
        }
        parseArgs(existLevelSet, pokerSells, new HashSet<>(), pokerSell, deep, sellType, targetSellType);
    }

    private static void parseArgs(Set<Integer> existLevelSet, List<PokerSell> pokerSells, Set<List<Poker>> pokersList, PokerSell pokerSell, int deep, SellType sellType, SellType targetSellType) {
        if (deep == 0) {
            List<Poker> allPokers = new ArrayList<>(pokerSell.getSellPokers());
            for (List<Poker> ps : pokersList) {
                allPokers.addAll(ps);
            }
            pokerSells.add(new PokerSell(allPokers, targetSellType, pokerSell.getEndIndex()));
            return;
        }
        for (int index = 0; index < pokerSells.size(); index++) {
            PokerSell subSell = pokerSells.get(index);
            if (subSell.getSellType() == sellType && !existLevelSet.contains(subSell.getEndIndex())) {
                pokersList.add(subSell.getSellPokers());
                existLevelSet.add(subSell.getEndIndex());
                parseArgs(existLevelSet, pokerSells, pokersList, pokerSell, deep - 1, sellType, targetSellType);
                existLevelSet.remove(subSell.getEndIndex());
                pokersList.remove(subSell.getSellPokers());
            }
        }
    }
}
