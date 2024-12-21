package buaa.oop.landlords.client.enums;

import lombok.Getter;

@Getter
public enum Assets {
    LANDLORDS("地主标志", 0),
    PEASANT("农民标志", 1),
    BTN_PLAY_CARD("出牌", 2),
    BTN_PASS("按钮不出", 3),
    BTN_YELLOW("黄色按钮", 4),
    BTN_GREEN("绿色按钮", 5),
    BTN_BLUE("蓝色按钮", 6),
    SCORE_ZERO("0分", 7),
    SCORE_ONE("1分", 8),
    SCORE_TWO("2分", 9),
    SCORE_THREE("3分", 10),
    SHOW_PASS("展示不出", 11),

    ;

    private final String name;
    private final Integer idx;

    Assets(String name, Integer idx) {
        this.name = name;
        this.idx = idx;
    }
}
