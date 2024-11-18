package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

/**
 * 若名称不合法 进入 ClientEventListener_CODE_CLIENT_NICKNAME_SET
 * 否则 进入 ClientEventListener_CODE_SHOW_OPTIONS
 */
@Slf4j
public class ServerEventListener_CODE_CLIENT_NICKNAME_SET extends ServerEventListener {
    @Override
    /**
     * @param data 为输入的 字符串 nickname
     */
    public void call(ClientEnd client, String data) {

        if (ServerContainer.containsClient(data)) {
            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_CLIENT_NICKNAME_SET, null, "The nickname is already in use");
        } else {
            log.info("Client {} | {} do set nickname to {}", client.getId(), client.getNickname(), data);
            client.setNickName(data);
            ServerContainer.addClient(client);
            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_SHOW_OPTIONS, null);
        }
    }
}
