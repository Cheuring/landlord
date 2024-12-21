package buaa.oop.landlords.client.enums;

import lombok.Getter;

@Getter
public enum Assets {
    LANDLORDS("地主标志", 0),
    PEASANT("农民标志", 1),
    PLAY_CARD("出牌", 2),
    PASS("不出", 3),
    BTN_YELLOW("黄色按钮", 4),
    BTN_GREEN("绿色按钮", 5),
    BTN_BLUE("蓝色按钮", 6),

    ;

    private final String name;
    private final Integer idx;

    Assets(String name, Integer idx) {
        this.name = name;
        this.idx = idx;
    }
}
