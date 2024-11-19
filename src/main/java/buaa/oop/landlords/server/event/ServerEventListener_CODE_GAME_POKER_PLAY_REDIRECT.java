package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前一个状态为：ClientEventListener_CODE_GAME_POKER_PLAY_PASS
 *            ClientEventListener_CODE_GAME_LANDLORD_CONFIRM
 *            ClientEventListener_CODE_GAME_POKER_PLAY
 *
 *下一个状态为：  ClientEventListener_CODE_GAME_POKER_PLAY_REDIRECT
 */
@Slf4j
public class ServerEventListener_CODE_GAME_POKER_PLAY_REDIRECT extends ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        Room room = ServerContainer.getRoom(clientEnd.getRoomId());
        Map<String, Object> dataMap = null;
        if(data != null) {
            dataMap = MapUtil.parse(data);
        }else{
            dataMap = new HashMap<>();
        }

        List<Map<String, Object>> clientInfos = new ArrayList<>(3);
        for(ClientEnd client : room.getClientEndList()) {
            if(clientEnd.getId() != client.getId()) {
                clientInfos.add(MapUtil.newInstance()
                        .put("clientId", client.getId())
                        .put("clientNickname", client.getNickname())
                        .put("role", client.getRole())
                        .put("surplus", client.getPokers().size())
                        .put("position", clientEnd.getPre() == client.getId() ? "UP" : "DOWN")
                        .map());
            }
        }

        // todo: 记牌器功能 for vip

        String result = MapUtil.newInstance()
                .put("pokers", clientEnd.getPokers())
                .put("lastSellPokers", dataMap.get("lastSellPokers"))
                .put("lastSellClientId", dataMap.get("lastSellClientId"))
                .put("clientInfos", clientInfos)
                .put("sellClientId", room.getCurrentSellClient())
                .put("sellClientNickname", ServerContainer.CLIENT_END_MAP.get(room.getCurrentSellClient()).getNickname())
                .json();
        try {
            SimplePrinter.printChatMsg("%s\n",SimplePrinter.RED,dataMap.get("lastSellClientId").toString());
        }catch (Exception e){
            SimplePrinter.printChatMsg("%s\n",SimplePrinter.RED,(String) dataMap.get("lastSellClientId"));
        }
       ChannelUtil.pushToClient(clientEnd.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_REDIRECT, result);
    }
}
