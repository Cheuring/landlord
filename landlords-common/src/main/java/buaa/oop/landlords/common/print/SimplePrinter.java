package buaa.oop.landlords.common.print;

import buaa.oop.landlords.common.entities.Poker;

import java.util.Date;
import java.util.List;
import buaa.oop.landlords.common.utils.PokerUtil;

public class SimplePrinter {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    public static void ServerLog(String msg) {
        System.out.printf("%n[Server] %s%n", msg);
    }

    public static void printNotice(String msg) {
        System.out.println(GREEN + msg + RESET);
    }

    public static void printWarning(String msg) {
        System.out.println(RED + msg + RESET);
    }

    public static void printChatMsg(String format ,String color,String... msg) {System.out.printf(color + format + RESET , (Object[]) msg);}
    public static void printPokers(List<Poker> pokers)
    {
        System.out.println(PokerUtil.printPokers(pokers));
    }

}
