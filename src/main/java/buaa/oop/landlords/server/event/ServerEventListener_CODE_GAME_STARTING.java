package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ClientStatus;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.common.utils.PokerUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *下一个状态为：ClientEventListener_CODE_GAME_LANDLORD_ELECT
 */
@Slf4j
public class ServerEventListener_CODE_GAME_STARTING extends ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        Room room = ServerContainer.getRoom(clientEnd.getRoomId());

        LinkedList<ClientEnd> clientEndList = room.getClientEndList();
        List<List<Poker>> pokerList = PokerUtil.distributePokers();

        for (int i = 0; i < clientEndList.size(); i++) {
            clientEndList.get(i).setPokers(pokerList.get(i));
        }
        room.setLandlordPokers(pokerList.get(3));

        int startPlayerIdx = new Random().nextInt(3);
        ClientEnd startPlayer = clientEndList.get(startPlayerIdx);
        room.setCurrentSellClient(startPlayer.getId());
        room.setFirstSellClient(startPlayer.getId());

        for(ClientEnd client: clientEndList){
            client.setStatus(ClientStatus.PLAYING);

            String result = MapUtil.newInstance()
                    .put("roomId", room.getId())
                    .put("roomOwner", room.getRoomOwner())
                    .put("roomClientCount", clientEndList.size())
                    .put("nextClientId", startPlayer.getId())
                    .put("nextClientNickname", startPlayer.getNickname())
                    .put("pokers", client.getPokers())
                    .put("highestScore", 0)
                    .json();

            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_STARTING, result);
        }
    }
}
