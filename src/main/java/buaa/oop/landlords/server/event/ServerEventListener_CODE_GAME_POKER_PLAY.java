package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import lombok.extern.slf4j.Slf4j;

/**
 *下一个状态为：ClientEventListener_CODE_SHOW_POKERS
 *       同时：若玩家牌出完 ClientEventListener_CODE_GAME_OVER
 *            否则进入 ServerEventListener_CODE_GAME_POKER_PLAY_REDIRECT
 */
@Slf4j
public class ServerEventListener_CODE_GAME_POKER_PLAY extends  ServerEventListener{
    @Override
    public void call(ClientEnd event, String data) {

    }
}
