package buaa.oop.landlords.client.event;

import io.netty.channel.Channel;
import buaa.oop.landlords.common.enums.ServerEventCode;

import java.util.Scanner;

public class ClientEventListener_CODE_CLIENT_NICKNAME_SET extends ClientEventListener {

    @Override
    public void call(Channel channel, String data) {
        System.out.println("Please set your nickname:");

        String name = new Scanner(System.in).nextLine();

        pushToServer(channel, ServerEventCode.CODE_CLIENT_NICKNAME_SET, name);
    }
}
