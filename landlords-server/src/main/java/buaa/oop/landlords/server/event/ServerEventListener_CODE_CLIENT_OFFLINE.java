package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.RoomStatus;
import buaa.oop.landlords.common.utils.ChannelUtil;
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
        if(room != null){
            room.removeClient(client);
            String result = MapUtil.newInstance()
                    .put("exitClientId", client.getId())
                    .put("exitClientNickname", client.getNickname())
                    .put("roomClientCount", room.getClientEndList().size())
                    .json();

            ServerContainer.ROOM_MAP.remove(room.getId());
            if(room.getStatus() == RoomStatus.STARTING){

                for(ClientEnd other: room.getClientEndList()){
                    ChannelUtil.pushToClient(other.getChannel(), ClientEventCode.CODE_EXIT, result);
                }
            }else{
                for(ClientEnd other: room.getClientEndList()){
                    ChannelUtil.pushToClient(other.getChannel(), ClientEventCode.CODE_ROOM_EXIT, result);
                }
            }
        }

        ServerContainer.CLIENT_END_MAP.remove(client.getId());
        log.info("Client {} | {} offline", client.getId(), client.getNickname());
    }
}
