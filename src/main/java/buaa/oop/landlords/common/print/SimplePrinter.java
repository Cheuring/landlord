package buaa.oop.landlords.common.print;

import buaa.oop.landlords.common.entities.Poker;

import java.util.Date;
import java.util.List;
import buaa.oop.landlords.common.utils.PokerUtil;

public class SimplePrinter {
    public static void ServerLog(String msg) {
        System.out.printf("%n[Server] %s%n", msg);
    }

    public static void printNotice(String msg) {
        System.out.println(msg);
    }

    public static void printChatMsg(String format ,String... msg) {System.out.printf(format, msg);}
    public static void printPokers(List<Poker> pokers)
    {
        System.out.println(PokerUtil.printPokers(pokers));
    }

}
