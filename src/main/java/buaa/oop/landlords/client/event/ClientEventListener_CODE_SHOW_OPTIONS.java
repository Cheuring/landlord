package buaa.oop.landlords.client.event;

import buaa.oop.landlords.common.enums.ServerEventCode;
import io.netty.channel.Channel;

import java.util.Scanner;

public class ClientEventListener_CODE_SHOW_OPTIONS extends ClientEventListener {

    @Override
    public void call(Channel channel, String data) {

//        System.exit(0);
        channel.close();
    }
}
