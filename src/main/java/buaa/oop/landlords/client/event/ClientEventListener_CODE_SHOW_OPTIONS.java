package buaa.oop.landlords.client.event;

import buaa.oop.landlords.common.enums.ServerEventCode;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * 根据输入进入
 *   ServerEventListener_CODE_ROOM_CREATE
 *   ServerEventListener_CODE_ROOM_GETALL
 *   ServerEventListener_CODE_ROOM_JOIN
 *   ServerEventListener_CODE_CLIENT_OFFLINE
 */
public class ClientEventListener_CODE_SHOW_OPTIONS extends ClientEventListener {

    @Override
    public void call(Channel channel, String data) {

//        System.exit(0);
        channel.close();
    }
}
