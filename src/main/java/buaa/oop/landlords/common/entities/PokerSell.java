package buaa.oop.landlords.common.entities;

import buaa.oop.landlords.common.enums.SellType;
import lombok.Getter;

import java.util.List;

public class PokerSell {

    @Getter
    private List<Poker> pokers;

    @Getter
    private SellType sellType;

    @Getter
    private int endIndex;

    public PokerSell(List<Poker> pokers, SellType sellType, int endIndex) {
        this.pokers = pokers;
        this.sellType = sellType;
        this.endIndex = endIndex;
    }
    public final List<Poker> getSellPokers() {
        return pokers;
    }
}
