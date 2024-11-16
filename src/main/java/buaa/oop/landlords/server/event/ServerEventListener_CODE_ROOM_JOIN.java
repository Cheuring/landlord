package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ClientStatus;
import buaa.oop.landlords.common.enums.RoomStatus;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Map;

@Slf4j
public class ServerEventListener_CODE_ROOM_JOIN extends ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        int roomId = Integer.parseInt(data);
        Map<String, Object> resultMap = MapUtil.newInstance()
                                            .put("roomId", roomId)
                                            .map();

        if(!ServerContainer.containsRoom(roomId)) {
            ChannelUtil.pushToClient(clientEnd.getChannel(), ClientEventCode.CODE_ROOM_JOIN_FAIL_BY_INEXIST, JsonUtil.toJson(resultMap));
            return;
        }

        Room room = ServerContainer.getRoom(roomId);
        LinkedList<ClientEnd> clientEndList = room.getClientEndList();

        int currentNum;
        if(clientEndList.size() >= 3 || (currentNum = room.addClient(clientEnd)) == -1) {
            ChannelUtil.pushToClient(clientEnd.getChannel(), ClientEventCode.CODE_ROOM_JOIN_FAIL_BY_FULL, JsonUtil.toJson(resultMap));
            return;
        }

        log.info("Client {} | {} join room {}", clientEnd.getId(), clientEnd.getNickName(), room.getId());

        clientEnd.setRoomId(roomId);
        clientEnd.setStatus(ClientStatus.READY);

        resultMap.put("clientId", clientEnd.getId());
        resultMap.put("clientNickname", clientEnd.getNickName());
        resultMap.put("roomClientCount", currentNum);

        for(ClientEnd client : clientEndList) {
            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_ROOM_JOIN_SUCCESS, JsonUtil.toJson(resultMap));
        }

        // todo: 没考虑执行这里的时候房间有人退出
        if(currentNum == 3) {
            clientEnd.setNext(clientEndList.getFirst());
            clientEndList.getFirst().setPre(clientEnd);
            room.setStatus(RoomStatus.STARTING);

            ServerEventListener.get(ServerEventCode.CODE_GAME_STARTING).call(clientEnd, String.valueOf(room.getId()));
        }
    }
}
