package buaa.oop.landlords.client;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.atomic.AtomicBoolean;

import static buaa.oop.landlords.client.event.ClientEventListener.get;

public class ChatRoom implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Object lock;
    private final Channel channel;
    private Thread thread;

    public ChatRoom(Channel channel,Object lock) {
        this.channel = channel;
        this.lock=lock;
        this.thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (lock) {
                while (!running.get()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            String content = SimpleWriter.write();
            if (content.equalsIgnoreCase("exit") || content.equalsIgnoreCase("e")) {
                SimplePrinter.printNotice("exit successfully");
                stop();
                synchronized (lock) {
                    lock.notify();
                }return;
            }
            String[] contents = spiltContent(content);
            if (contents != null) {
                String result = MapUtil.newInstance()
                        .put("ClientTo", contents[0])
                        .put("Content", contents[1]).json();

                ChannelUtil.pushToServer(channel, ServerEventCode.CODE_CHAT, result);
            }
        }
    }

    public void start() {
        synchronized (lock) {
            running.set(true);
            lock.notify();
        }
    }

    public void stop() {
        running.set(false);
        Thread.interrupted();
    }

    public static String[] spiltContent(String content) {
        String[] info = new String[2];
        if (content == null || content.length() == 0) {
            SimplePrinter.printNotice("No information");
        } else {
            if (content.startsWith("@")) {
                int space = content.indexOf(" ");
                if (space > 0) {
                    info[0] = content.substring(1, space);
                    info[1] = content.substring(space + 1);
                    return info;
                }
            }
            SimplePrinter.printNotice("Your information format is incorrect");
        }
        return null;
    }
}
