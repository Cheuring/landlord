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

    public ChatRoom(Channel channel){
        this.channel = channel;
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
             result 包含ClientTo, ClientFrom, Content
             */
            String result = MapUtil.newInstance()
                    .put("ClientFrom", User.getINSTANCE().getId())
                    .put("ClientTo", contents[0])
                    .put("Content", contents[1]).json();

            ChannelUtil.pushToServer(channel, ServerEventCode.CODE_CHAT, result);
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
    public static String[] spiltContent(String content){
        String [] info;
        if(content==null || content.length()==0){
            SimplePrinter.printNotice("No information");
        }else{
            if(content.startsWith("@")){
                info = content.split(" ");
                info[0].substring(1);
                return info;
            }else{

                SimplePrinter.printNotice("Your information format is incorrect");
            }
        }
    }
}
