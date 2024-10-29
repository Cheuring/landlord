package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerEventListener_CODE_CLIENT_OFFLINE extends ServerEventListener{
    @Override
    public void call(ClientEnd event, String data) {
        Room room = ServerContainer.ROOM_MAP.get(event.getRoomId());

        ServerContainer.CLIENT_END_MAP.remove(event.getId());

        log.info("Client {} | {} offline", event.getId(), event.getNickName());
    }
}
