package buaa.oop.landlords.common.print;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimpleWriter {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final EventLoop eventLoop = new DefaultEventLoop();

    public static String write(String nickname, String message) {
        System.out.println();
        System.out.printf("[%s@%s]$ ", nickname, message);
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

    public static Future<String> writeAsync() {
        return eventLoop.submit(() -> write());
    }

    public static void shutdown() {
        eventLoop.shutdownGracefully();
    }
}
