package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerEventListener_CODE_CLIENT_NICKNAME_SET extends ServerEventListener{
    @Override
    public void call(ClientEnd client, String data) {
        // todo: check name validity

        client.setNickName(data);

        log.info("Client {} | {} do set nickname to {}", client.getId(), client.getNickName(), data);

        ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_SHOW_OPTIONS, null);
    }
}
