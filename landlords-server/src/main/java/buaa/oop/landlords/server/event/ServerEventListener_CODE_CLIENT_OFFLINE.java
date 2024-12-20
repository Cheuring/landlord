package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerEventListener_CODE_CLIENT_OFFLINE extends ServerEventListener{
    @Override
    public void call(ClientEnd client, String data) {
        Room room = ServerContainer.ROOM_MAP.get(client.getRoomId());
        if(room != null){
            ServerEventListener_CODE_ROOM_EXIT.roomExit(client, room);
        }

        ServerContainer.CLIENT_END_MAP.remove(client.getId());
        ServerContainer.CLIENT_NAME_TO_ID.remove(client.getNickname());
        log.info("Client {} | {} offline", client.getId(), client.getNickname());
    }
}
