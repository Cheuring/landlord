package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerEventListener_CODE_ROOM_JOIN extends ServerEventListener{
    @Override
    public void call(ClientEnd client, String data) {
    }
}
