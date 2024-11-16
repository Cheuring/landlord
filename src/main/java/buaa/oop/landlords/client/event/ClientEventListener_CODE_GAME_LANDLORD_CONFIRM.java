package buaa.oop.landlords.client.event;

import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

/**
 *下一个状态为, ServerEventListener_CODE_GAME_POKER_PLAY_REDIRECT
 */
public class ClientEventListener_CODE_GAME_LANDLORD_CONFIRM extends ClientEventListener{
    @Override
    /**
     * @param data (map type) includes room info,landlord name,additional poker(3) and baseScore
     */
    public void call(Channel channel, String data) {
        Map<String, Object> roominfo = MapUtil.parse(data);
        String landlordNickname = String.valueOf(roominfo.get("landlordNickname"));
        int baseScore = (Integer) roominfo.get("baseScore");
        String baseScoreString;

        if (baseScore == 1) {
            baseScoreString = "1 score";
        } else {
            baseScoreString = baseScore + " scores";
        }

        SimplePrinter.printNotice(landlordNickname + " has become the landlord with " + baseScoreString + " and gotten three extra cards");

        List<Poker> additionalPokers = JsonUtil.fromJson(JsonUtil.toJson(roominfo.get("additionalPokers")),new TypeReference<List<Poker>>(){});
       //todo printPokers(list<Poker>) in Poker
       // SimplePrinter.printPokers(additionalPokers);

        pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT);

    }
}
