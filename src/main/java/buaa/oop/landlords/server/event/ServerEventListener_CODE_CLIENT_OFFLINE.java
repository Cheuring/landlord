package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.RoomStatus;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

/**
 *data 没有被用到
 */
@Slf4j
public class ServerEventListener_CODE_CLIENT_OFFLINE extends ServerEventListener{
    @Override
    public void call(ClientEnd client, String data) {
        Room room = ServerContainer.ROOM_MAP.get(client.getRoomId());
        if(room.getStatus() == RoomStatus.STARTING){
            // todo: 通知其他玩家 游戏结束
        }

        ServerContainer.CLIENT_END_MAP.remove(client.getId());

        log.info("Client {} | {} offline", client.getId(), client.getNickName());
    }
}
