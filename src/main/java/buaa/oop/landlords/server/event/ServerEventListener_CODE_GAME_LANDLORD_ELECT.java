package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import lombok.extern.slf4j.Slf4j;

/**
 *如果最高分不为0：下一个状态为 ClientEventListener_CODE_GAME_LANDLORD_CONFIRM
 *如果最高分为0：下一个状态为 ClientEventListener_CODE_GAME_LANDLORD_CYCLE 同时 ServerEventListener_CODE_GAME_STARTING
 *
 */
@Slf4j
public class ServerEventListener_CODE_GAME_LANDLORD_ELECT extends ServerEventListener{
    @Override
    public void call(ClientEnd event, String data) {

    }
}
