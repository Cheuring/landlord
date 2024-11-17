package buaa.oop.landlords.common.utils;

import buaa.oop.landlords.common.entities.Poker;

import java.util.Comparator;
import java.util.List;

public class PokerUtil {
    private static final Comparator<Poker> pokerComparator = (o1, o2) -> o1.getLevel().getLevel() - o2.getLevel().getLevel();
    /**
     * 发牌
     * @return 三个玩家的牌 3*17+3
     */
    public static List<List<Poker>> distributePokers() {
        return null;
    }

    /**
     * 排序
     * @param pokers 牌
     */
    public static void sortPokers(List<Poker> pokers) {
        pokers.sort(pokerComparator);
    }
}
