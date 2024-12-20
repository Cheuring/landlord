package buaa.oop.landlords.common.print;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimpleWriter {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String write(String nickname, String message) {
        SimplePrinter.printPrompt(String.format("%n[%s@%s]$ ", nickname, message));
        try {
            return write();
        } finally {
            System.out.println();
        }

    }

    public static String write() {
        try {
            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
