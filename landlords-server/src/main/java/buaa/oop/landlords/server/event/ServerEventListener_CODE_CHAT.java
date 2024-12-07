package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ServerEventListener_CODE_CHAT extends ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        Map<String, Object> map = MapUtil.parse(data);
        String clientToName = (String) map.get("ClientTo");
        map.put("ClientFrom", clientEnd.getNickname());

        if(!ServerContainer.containsClient(clientToName)) {
            ChannelUtil.pushToClient(clientEnd.getChannel(), ClientEventCode.CODE_CHAT_FAIL, null);
        }

        ChannelUtil.pushToClient(ServerContainer.getClient(clientToName).getChannel(), ClientEventCode.CODE_CHAT, JsonUtil.toJson(map));
    }
}
