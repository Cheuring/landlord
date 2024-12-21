package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static buaa.oop.landlords.client.ClientContainer.gameRoom;
import static buaa.oop.landlords.client.event.ClientEventListener_CODE_CLIENT_NICKNAME_SET.NICKNAME_MAX_LENGTH;

/**
 *如果是出牌回合：ServerEventListenerEventListener_CODE_GAME_POKER_PLAY
 */
@Slf4j
public class ClientEventListener_CODE_GAME_POKER_PLAY_REDIRECT extends ClientEventListener{
    private static String[] choose = new String[]{"UP", "DOWN"};

    private static String format = "\n[%-4s] %-" + NICKNAME_MAX_LENGTH + "s  surplus %-2s ";
    @Override
    /**
     * @param data includes last player's pokers used and his id,all players info, current player's id and nickname
     */
    public void call(Channel channel, String data) {
        Map<String, Object> roominfo= MapUtil.parse(data);
        int sellClientId = (int) roominfo.get("sellClientId");
        List<Poker> pokers = JsonUtil.fromJson((String)roominfo.get("pokers"),new TypeReference<List<Poker>>(){});
        List<Map<String, Object>> clientInfos = JsonUtil.fromJson((String) roominfo.get("clientInfos"), new TypeReference<List<Map<String, Object>>>() {
        });

        for (int index = 0; index < 2; index++) {
            for (Map<String, Object> clientInfo : clientInfos) {
                String position = (String) clientInfo.get("position");
                if (position.equalsIgnoreCase(choose[index])) {
                    System.out.printf(format, clientInfo.get("position"), clientInfo.get("clientNickname"), clientInfo.get("surplus"));
                }
            }
        }

        SimplePrinter.printNotice("");
        if(sellClientId== User.getINSTANCE().getId()){
            Platform.runLater( () -> {
                gameRoom.displayPokers(pokers);
            });
            get(ClientEventCode.CODE_GAME_POKER_PLAY).call(channel, data);
        }
        else{
            List<Poker>myPokers=JsonUtil.fromJson((String) roominfo.get("pokers"),new TypeReference<List<Poker>>() {});
            SimplePrinter.printPokers(myPokers);
            SimplePrinter.printNotice("It is " + roominfo.get("sellClientNickname") + "  \'s turn. Please wait for him to play his cards.");
        }

    }
}
