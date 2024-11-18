package buaa.oop.landlords.client;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.client.event.ClientEventListener;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

@Slf4j
public class ChatRoom {
    private final Channel channel;
    private static ChatRoom instance;
    private final ExecutorService executorService;
    private Future<?> chatTaskFuture;
    private volatile boolean isRunning = false;
    private ClientEventCode code;

    private ChatRoom(Channel channel) {
        this.channel = channel;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public static ChatRoom getInstance(Channel channel) {
        if (instance == null) {
            instance = new ChatRoom(channel);
        }
        return instance;
    }

    public void start(ClientEventCode endCode) {
        if (isRunning) {
            return;
        }
        this.code = endCode;
        isRunning = true;
        chatTaskFuture = executorService.submit(new ChatTask());
    }

    public void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        chatTaskFuture.cancel(false);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    private class ChatTask implements Runnable {
        @Override
        public void run() {
            while(isRunning && !Thread.currentThread().isInterrupted()) {
                String userInput = SimpleWriter.write(User.INSTANCE.getNickname(), "ChatRoom");
                if(userInput == null || (userInput = userInput.trim()).isEmpty()) {
                    continue;
                }

                int idx;
                if(userInput.charAt(0) == 'e') {
                    if(code != null){
                        channel.eventLoop().execute(() -> ClientEventListener.get(code).call(channel, null));
                    }
                    stop();
                    break;
                }else if(userInput.charAt(0) != '@' || (idx = userInput.indexOf(' ')) == -1) {
                    SimplePrinter.printNotice("Please enter your content in such format\n@[ClientToName] [Content]");
                    continue;
                }

                String result = MapUtil.newInstance()
                        .put("ClientTo", userInput.substring(1, idx))
                        .put("Content", userInput.substring(idx + 1))
                        .json();

                ChannelUtil.pushToServer(channel, ServerEventCode.CODE_CHAT, result);
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
