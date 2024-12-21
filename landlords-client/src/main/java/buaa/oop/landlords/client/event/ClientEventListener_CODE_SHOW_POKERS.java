package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.client.enums.Assets;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.ClientRole;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;

import static buaa.oop.landlords.client.ClientContainer.gameRoom;

/**
 *
 */
public class ClientEventListener_CODE_SHOW_POKERS extends ClientEventListener{

    @Override
    /**
     * @param data includes currentPlayerNickname,currentPlayerId and used pokers,nextPlayerNickname,nextPlayerId
     */
    public void call(Channel channel, String data) {
        Map<String, Object>showPokers= MapUtil.parse(data);
        List<Poker> pokers = JsonUtil.fromJson((String)showPokers.get("pokers"), new TypeReference<List<Poker>>(){});

        SimplePrinter.printNotice((String)showPokers.get("lastSellClientName")+ "["+JsonUtil.fromJson((String)showPokers.get("role"), ClientRole.class).name()+"]"+"used ");
        SimplePrinter.printPokers(pokers);
        Platform.runLater(() -> {
            if (showPokers.get("lastSellClientName").equals(gameRoom.getPlayerName(1)))
                gameRoom.updatePlayerArea(pokers, null, gameRoom.getPlayerLastPokers(1));
            else if (showPokers.get("lastSellClientName").equals(gameRoom.getPlayerName(3)))
                gameRoom.updatePlayerArea(pokers, null, gameRoom.getPlayerLastPokers(3));
        });
        if (showPokers.containsKey("nextPlayerId")) {
            if (User.getINSTANCE().getId() != (int) showPokers.get("nextPlayerId")) {
                SimplePrinter.printNotice("It's " + (String) showPokers.get("nextPlayerNickname") + "'s turn.");
            }
        }
    }
}
