package buaa.oop.landlords.client.event;

import io.netty.channel.Channel;

/**
 * 参数为新创建的room
 * 无下一个状态
 */
public class ClientEventListener_CODE_ROOM_CREATE_SUCCESS extends ClientEventListener{
    @Override
    public void call(Channel channel, String data) {

    }
}
