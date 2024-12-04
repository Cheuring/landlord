package buaa.oop.landlords.common.enums;

public enum PokerType {
    BLANK(" "),

    DIAMOND("♦"),

    CLUB("♣"),

    SPADE("♠"),

    HEART("♥");

    private final String name;

    PokerType(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }
}
