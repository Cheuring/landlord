package buaa.oop.landlords.test;

import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.PokerUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestCard {
    @Test
    public void testCardSerialization() {
        List<List<Poker>> lists = PokerUtil.distributePokers();
        String json = JsonUtil.toJson(lists.get(0));

        List<Poker> pokers = JsonUtil.fromJson(json, new TypeReference<List<Poker>>() {
        });
//        System.out.println(pokers);
        SimplePrinter.printPokers(pokers);
    }
}
