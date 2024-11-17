package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ServerEventListener_CODE_CHAT extends ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        Map<String, Object> map = MapUtil.parse(data);
        int clientToId = (int) map.get("ClientTo");

        if(!ServerContainer.containsClient(clientToId)){
            ChannelUtil.pushToClient(clientEnd.getChannel(), ClientEventCode.CODE_CHAT_FAIL, null, "Client not exist");
        }

        ChannelUtil.pushToClient(ServerContainer.getClient(clientToId).getChannel(), ClientEventCode.CODE_CHAT, data);
    }
}
