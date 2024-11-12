package buaa.oop.landlords.client.event;

import io.netty.channel.Channel;

/**
 *等待输入：
 * 若输入无效 重新调用该状态
 * 若有效 pass 进入 ServerEventListener_CODE_GAME_POKER_PLAY_PASS
 *       exit 进入 ServerEventListener_CODE_CLIENT_EXIT
 *       view 重新打印出牌信息,重新调用该状态
 *       出牌合法 进入 ServerEventListener_CODE_GAME_POKER_PLAY
 */
public class ClientEventListener_CODE_GAME_POKER_PLAY extends ClientEventListener{
    @Override
    public void call(Channel channel, String data) {

    }
}
