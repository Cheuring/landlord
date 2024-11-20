package buaa.oop.landlords.client.event;

import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

/**
 * 下一个状态：ClientEventListener_CODE_GAME_LANDLORD_ELECT
 */
public class ClientEventListener_CODE_GAME_STARTING extends ClientEventListener{
    @Override
    /**
     * String data = MapUtil.newInstance()
     *                     .put("roomId", room.getId())
     *                     .put("roomOwner", room.getRoomOwner())
     *                     .put("roomClientCount", clientEndList.size())
     *                     .put("nextClientId", startPlayer.getId())
     *                     .put("nextClientNickname", startPlayer.getNickName())
     *                     .put("pokers", client.getPokers())
     *                     .put("highestScore", 0)
     *                     .json();
     */
    public void call(Channel channel, String data) {
        Map<String, Object> map = MapUtil.parse(data);

        SimplePrinter.printNotice("Game starting!");

        List<Poker> pokers = JsonUtil.fromJson((String) map.get("pokers"),new TypeReference<List<Poker>>(){});

        SimplePrinter.printNotice("");
        SimplePrinter.printNotice("Your cards are:");
        SimplePrinter.printPokers(pokers);

        get(ClientEventCode.CODE_GAME_LANDLORD_ELECT).call(channel, data);
    }
}
