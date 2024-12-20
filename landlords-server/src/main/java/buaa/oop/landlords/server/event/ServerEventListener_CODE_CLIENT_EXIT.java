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
 * String result = MapUtil.newInstance()
 *                 .put("winnerNickname", winner.getNickname())
 *                 .put("winnerType", winner.getRole())
 *                 .put("scores", clientScores)
 *                 .json();
 */
@Slf4j
public class ServerEventListener_CODE_CLIENT_EXIT extends ServerEventListener{
    private static final Object locked = new Object();
    @Override
    public void call(ClientEnd event, String data) {
        synchronized (locked){
            Map<String,Object>datas=MapUtil.parse(data);
            Room room = ServerContainer.getRoom(event.getRoomId());
            if (room == null) {
                return;
            }
            String result = MapUtil.newInstance()
                    .put("status", datas.get("status"))
                    .put("roomId", room.getId())
                    .put("exitClientId", event.getId())
                    .put("exitClientNickname", event.getNickname())
                    .put("winnerNickname", datas.get("winnerNickname"))
                    .put("winnerType",datas.get("winnerType"))
                    .put("scores",datas.get("scores"))
                    .json();
            for (ClientEnd client : room.getClientEndList()) {
                    ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_OVER, result);
                 //   client.init();
            }

            ServerContainer.removeRoom(room.getId());
        }
    }
}
