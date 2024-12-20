package buaa.oop.landlords.common.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum PokerLevel {
    LEVEL_A(14, "A", new Character[]{'A', 'a', '1'}, 0),

    LEVEL_2(15, "2", new Character[]{'2'}, 1),

    LEVEL_3(3, "3", new Character[]{'3'}, 2),

    LEVEL_4(4, "4", new Character[]{'4'}, 3),

    LEVEL_5(5, "5", new Character[]{'5'}, 4),

    LEVEL_6(6, "6", new Character[]{'6'}, 5),

    LEVEL_7(7, "7", new Character[]{'7'}, 6),

    LEVEL_8(8, "8", new Character[]{'8'}, 7),

    LEVEL_9(9, "9", new Character[]{'9'}, 8),

    LEVEL_10(10, "10", new Character[]{'T', 't', '0'}, 9),

    LEVEL_J(11, "J", new Character[]{'J', 'j'}, 10),

    LEVEL_Q(12, "Q", new Character[]{'Q', 'q'}, 11),

    LEVEL_K(13, "K", new Character[]{'K', 'k'}, 12),

    LEVEL_SMALL_KING(16, "S", new Character[]{'S', 's'}, 0),

    LEVEL_BIG_KING(17, "X", new Character[]{'X', 'x'}, 1),
    ;

    private int level;
    private int idx;

    private String name;

    private Character[] alias;

    private static Set<Character> aliasSet = new HashSet<>();

    static {
        for (PokerLevel level : PokerLevel.values()) {
            PokerLevel.aliasSet.addAll(Arrays.asList(level.getAlias()));
        }
    }

    PokerLevel(int level, String name, Character[] alias, int idx) {
        this.level = level;
        this.name = name;
        this.alias = alias;
        this.idx = idx;
    }

    public static boolean aliasContains(char key)
    {
        return aliasSet.contains(key);
    }

    public final Character[] getAlias() {
        return alias;
    }

    public final String getName() {
        return name;
    }

    public final int getLevel() {
        return level;
    }

    public final int getIdx() {
        return idx;
    }
}
