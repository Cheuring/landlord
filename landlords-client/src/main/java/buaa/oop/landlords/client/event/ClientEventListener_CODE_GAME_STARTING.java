package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.GUI.GameRoom;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;
import javafx.application.Platform;

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
        Map<String, Object> upData = MapUtil.parse((String)map.get("up"));
        Map<String, Object> downData = MapUtil.parse((String)map.get("down"));
        User user = User.getINSTANCE();

        SimplePrinter.printNotice("Game starting!");
        List<Poker> pokers = JsonUtil.fromJson((String) map.get("pokers"),new TypeReference<List<Poker>>(){});
        Platform.runLater(() -> {
            GameRoom.setRoomStatus("游戏状态: 选择地主");
            GameRoom.setPlayerName((String)upData.get("name"), 1);
            GameRoom.setPlayerName((String)downData.get("name"), 3);
            GameRoom.setPoint(String.valueOf(upData.get("score")), 1);
            GameRoom.setPoint(String.valueOf(downData.get("score")), 3);
            GameRoom.setPoint(String.valueOf(user.getScore()), 2);
            GameRoom.displayPokers(pokers);
            GameRoom.updatePokers(17,1);
            GameRoom.updatePokers(17,3);
        });
        SimplePrinter.printNotice("");
        SimplePrinter.printNotice("Your cards are:");
        SimplePrinter.printPokers(pokers);

        get(ClientEventCode.CODE_GAME_LANDLORD_ELECT).call(channel, data);
    }

}
