package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 下一个：ClientEventListener_CODE_SHOW_ROOMS
 */
@Slf4j
public class ServerEventListener_CODE_ROOM_GETALL extends ServerEventListener {

    @Override
    public void call(ClientEnd clientEnd, String data) {
        List<Map<String, Object>> roomList = new ArrayList<>(ServerContainer.getRoomMap().size());

        for (var entry : ServerContainer.getRoomMap().entrySet()) {
            var room = entry.getValue();
            roomList.add(MapUtil.newInstance()
                    .put("roomId", room.getId())
                    .put("roomOwner", room.getRoomOwner())
                    .put("roomClientCount", room.getClientEndList().size())
                    .put("roomStatus", room.getStatus())
                    .map());
        }

        ChannelUtil.pushToClient(clientEnd.getChannel(), ClientEventCode.CODE_SHOW_ROOMS, JsonUtil.toJson(roomList));
    }
}
