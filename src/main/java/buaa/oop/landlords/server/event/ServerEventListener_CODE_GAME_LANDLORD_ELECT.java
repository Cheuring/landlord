package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ClientRole;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.common.utils.PokerUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 *如果最高分不为0：下一个状态为 ClientEventListener_CODE_GAME_LANDLORD_CONFIRM
 *如果最高分为0：下一个状态为 ClientEventListener_CODE_GAME_LANDLORD_CYCLE 同时 ServerEventListener_CODE_GAME_STARTING
 *
 */
@Slf4j
public class ServerEventListener_CODE_GAME_LANDLORD_ELECT extends ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        Map<String, Object> map = MapUtil.parse(data);
        Room room = ServerContainer.getRoom(clientEnd.getRoomId());
        int highestScore = (Integer)map.get("highestScore");

        if (room == null) {
            return;
        }

        if (highestScore == 3) {
            room.setBaseScore(highestScore);
            confirmLandlord(clientEnd, room);
            return;
        }

        if(clientEnd.getNext().getId() == room.getFirstSellClient()){
            if(highestScore == 0) {
                for (ClientEnd client : room.getClientEndList()) {
                    ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_LANDLORD_CYCLE, null);
                }
                ServerEventListener.get(ServerEventCode.CODE_GAME_STARTING).call(clientEnd, null);
                return;
            }

            room.setBaseScore(highestScore);
            int landlordId = (Integer)map.get("currentLandlordId");
            for (ClientEnd client : room.getClientEndList()) {
                if (client.getId() == landlordId) {
                    confirmLandlord(client, room);
                    return;
                }
            }
        }

        ClientEnd next = clientEnd.getNext();
        room.setCurrentSellClient(next.getId());
        String result;

        if(highestScore != 0){
            result = MapUtil.newInstance()
                    .put("roomId", room.getId())
                    .put("preClientNickname", clientEnd.getNickName())
                    .put("preClientId", clientEnd.getId())
                    .put("nextClientId", next.getId())
                    .put("nextClientNickname", next.getNickName())
                    .put("highestScore", highestScore)
                    .put("currentLandlordId", (Integer)map.get("currentLandlordId"))
                    .json();
        }else{
            result = MapUtil.newInstance()
                    .put("roomId", room.getId())
                    .put("preClientNickname", clientEnd.getNickName())
                    .put("nextClientId", next.getId())
                    .put("nextClientNickname", next.getNickName())
                    .put("highestScore", highestScore)
                    .json();
        }

        for(ClientEnd client: room.getClientEndList()){
            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_LANDLORD_ELECT, result);
        }

    }

    private void confirmLandlord(ClientEnd clientEnd, Room room) {
        clientEnd.getPokers().addAll(room.getLandlordPokers());
        PokerUtil.sortPokers(clientEnd.getPokers());

        int landlordId = clientEnd.getId();
        room.setCurrentSellClient(landlordId);
        room.setLandlordId(landlordId);
        room.setFirstSellClient(landlordId);
        clientEnd.setRole(ClientRole.LANDLORD);

        for(ClientEnd client: room.getClientEndList()){
            String result = MapUtil.newInstance()
                    .put("roomId", room.getId())
                    .put("landlordNickname", clientEnd.getNickName())
                    .put("landlordId", landlordId)
                    .put("additionalPokers", room.getLandlordPokers())
                    .put("baseScore", room.getBaseScore())
                    .json();
            client.resetRound();

            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_LANDLORD_CONFIRM, result);
        }
    }
}
