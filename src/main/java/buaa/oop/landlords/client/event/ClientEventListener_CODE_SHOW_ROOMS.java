package buaa.oop.landlords.client.event;

import io.netty.channel.Channel;

/**
 * 解析data为房间信息的List<Map<String, Object>>并打印
 * 下一个状态：ClientEventListener_CODE_SHOW_OPTIONS_PVP
 *
 */
public class ClientEventListener_CODE_SHOW_ROOMS extends ClientEventListener{
    @Override
    public void call(Channel channel, String data) {

    }
}
