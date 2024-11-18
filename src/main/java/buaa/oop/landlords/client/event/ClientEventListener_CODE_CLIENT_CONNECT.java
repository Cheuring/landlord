package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.ChatRoom;
import buaa.oop.landlords.client.SimpleClient;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.print.SimplePrinter;
import io.netty.channel.Channel;

public class ClientEventListener_CODE_CLIENT_CONNECT extends ClientEventListener{
    @Override
    public void call(Channel channel, String data) {
        SimplePrinter.ServerLog("Connected to server. Welcome !");
        User.INSTANCE.setId(Integer.parseInt(data));
    }
}
