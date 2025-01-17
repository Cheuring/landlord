package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 *所有客户端 ClientEvenListener_CODE_GAME_POKER_PLAY_PASS
 */
@Slf4j
public class ServerEventListener_CODE_GAME_POKER_PLAY_PASS extends ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        Room room = ServerContainer.getRoom(clientEnd.getRoomId());
        Map<String, Object> map = MapUtil.parse(data);

        assert room != null;
        assert room.getCurrentSellClient() == clientEnd.getId();
        assert clientEnd.getId() != room.getLastSellClient();

        ClientEnd next = ServerContainer.getClient(clientEnd.getNext());
        room.setCurrentSellClient(next.getId());

        for (ClientEnd client : room.getClientEndList()) {
            String result = MapUtil.newInstance()
                    .put("clientId", clientEnd.getId())
                    .put("clientNickname", clientEnd.getNickname())
                    .put("nextClientId", next.getId())
                    .put("nextClientNickname", next.getNickname())
                    .put("lastSellPokers", map.get("lastSellPokers"))
                    .put("lastSellClientId", map.get("lastSellClientId"))
                    .put("lastSellClientName", map.get("lastSellClientName"))
                    .put("role", map.get("role"))
                    .json();

            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_PASS, result);
        }


    }
}
