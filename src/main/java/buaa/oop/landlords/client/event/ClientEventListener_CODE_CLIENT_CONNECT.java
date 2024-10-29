package buaa.oop.landlords.client.event;

import io.netty.channel.Channel;

public class ClientEventListener_CODE_CLIENT_CONNECT extends ClientEventListener{
    @Override
    public void call(Channel channel, String data) {
        System.out.println("Connected to server. Welcome !");
    }
}
