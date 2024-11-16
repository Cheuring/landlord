package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;

import java.util.Map;

/**
 *下一个状态为 ServerEventListener_CODE_GAME_POKER_PLAY_REDIRECT
 */
public class ClientEventListener_CODE_GAME_POKER_PLAY_PASS extends ClientEventListener{
    @Override
    /**
     * @param data include pass and next player's id and nickname
     */
    public void call(Channel channel, String data) {
        Map<String,Object>players= MapUtil.parse(data);
        SimplePrinter.printNotice(players.get("clientNickname")+"passed");
        SimplePrinter.printNotice(players.get("It's"+"nextClientNickname")+"'s turn");
        int turnClientId = (int) players.get("nextClientId");
        if (User.getINSTANCE().getId() == turnClientId) {
            pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT);
        }
    }
}
