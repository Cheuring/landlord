package buaa.oop.landlords.client.event;

import io.netty.channel.Channel;

/**
 *如果退出：下一个状态ClientEventListener_CODE_GAME_LANDLORD_ELECT
 *如果选择分数：下一个状态为ServerEventListener_CODE_GAME_LANDLORD_ELECT,发送参数为 score分数
 */
public class ClientEventListener_CODE_GAME_LANDLORD_ELECT extends ClientEventListener{
    @Override
    public void call(Channel channel, String data) {

    }
}
