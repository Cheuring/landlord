package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;

import java.util.Map;

public class ClientEventListener_CODE_ROOM_EXIT extends ClientEventListener{
    @Override
    /**
     * @param data    String result = MapUtil.newInstance()
     *                     .put("exitClientId", client.getId())
     *                     .put("exitClientNickname", client.getNickname())
     *                     .put("roomClientCount", room.getClientEndList().size())
     *                     .json();
     */
    public void call(Channel channel, String data) {
        Map<String, Object> map = MapUtil.parse(data);

        SimplePrinter.printNotice(String.format("\n%s left the room.\nCurrent players in the room: %s\n", map.get("exitClientNickname"), map.get("roomClientCount")));
    }
}
