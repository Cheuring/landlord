package buaa.oop.landlords.common.entities;

import buaa.oop.landlords.common.enums.SellType;
import buaa.oop.landlords.common.utils.PokerUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class PokerSell {
    @Getter
    private int score;

    @Getter
    private List<Poker> pokers;

    @Getter
    private SellType sellType;

    @Getter
    private int endIndex;

    public PokerSell(List<Poker> pokers, SellType sellType, int endIndex) {
        this.score = PokerUtil.parseScore(sellType, endIndex);
        this.pokers = pokers;
        this.sellType = sellType;
        this.endIndex = endIndex;
    }
}
