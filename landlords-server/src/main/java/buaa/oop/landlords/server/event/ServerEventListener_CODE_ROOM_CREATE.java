package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ClientStatus;
import buaa.oop.landlords.common.enums.RoomStatus;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

/**
 *下一个状态为ClientEventListener_CODE_ROOM_CREATE_SUCCESS
 */
@Slf4j
public class ServerEventListener_CODE_ROOM_CREATE extends ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        Room room = new Room(ServerContainer.getNewRoomId());
        room.setRoomOwner(clientEnd.getNickname());
        room.setStatus(RoomStatus.WAIT);
        room.addClient(clientEnd);
        room.setCurrentSellClient(clientEnd.getId());

        clientEnd.setRoomId(room.getId());
        ServerContainer.addRoom(room);

        clientEnd.setStatus(ClientStatus.NO_READY);

        log.info("Client {} | {} create room {}", clientEnd.getId(), clientEnd.getNickname(), room.getId());
        ChannelUtil.pushToClient(clientEnd.getChannel(), ClientEventCode.CODE_ROOM_CREATE_SUCCESS, String.valueOf(room.getId()));
    }
}
