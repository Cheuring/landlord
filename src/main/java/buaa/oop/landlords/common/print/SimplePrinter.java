package buaa.oop.landlords.common.print;

public class SimplePrinter {
    public static void ServerLog(String msg) {
        System.out.printf("%n[Server] %s%n", msg);
    }

    public static void printNotice(String msg) {
        System.out.println(msg);
    }
}
