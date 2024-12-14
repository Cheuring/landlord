package buaa.oop.landlords.common.enums;

public enum PokerType {
    BLANK(" ", -1),

    DIAMOND("♦", 1),

    CLUB("♣", 0),

    SPADE("♠", 3),

    HEART("♥", 2);

    private final String name;
    private final int value;

    PokerType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public final String getName() {
        return name;
    }

    public final int getValue() {
        return value;
    }
}
