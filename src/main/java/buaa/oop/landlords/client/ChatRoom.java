package buaa.oop.landlords.client;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicBoolean;

public class ChatRoom implements Runnable{
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Object lock = new Object();
    private final Channel channel;
    private Thread thread;

    public ChatRoom(Channel channel){
        this.channel = channel;
        this.thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
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
            String[] contents = spiltContent(content);
            /*
             todo: 处理聊天内容
             result 包含ClientTo, Content
             */
           if(contents!=null){
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
    }

    public static String[] spiltContent(String content) {
        String[] info=new String[2];
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
