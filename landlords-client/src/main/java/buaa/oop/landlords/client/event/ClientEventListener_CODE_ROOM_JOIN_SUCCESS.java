package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.GUI.GameRoom;
import buaa.oop.landlords.client.GUI.RoomHall;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import buaa.oop.landlords.client.entities.User;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Map;

import static buaa.oop.landlords.client.ClientContainer.gameRoom;

/**
 * 等待人数满足 3
 */
public class ClientEventListener_CODE_ROOM_JOIN_SUCCESS extends ClientEventListener{
    @Override
    /**
     * @param data is json,original type is map,map store the room info
     */
    public void call(Channel channel, String data) {
        Map<String, Object> map = MapUtil.parse(data);

        User user = User.getINSTANCE();
        int joinClientId = (int) map.get("clientId");
        if (User.getINSTANCE().getId() == joinClientId) {
            SimplePrinter.printNotice("You have joined room：" +   map.get("roomId")+". There are " + map.get("roomClientCount") + " players in the room now.");
            SimplePrinter.printNotice("Please wait for other players to join. The game would start at three players!");
        } else {
            SimplePrinter.printNotice(map.get("clientNickname") + " joined room, there are currently " + map.get("roomClientCount") + " in the room.");
            Platform.runLater(()->{
                gameRoom.setPlayerName((String) map.get("clientNickname"), 1);
            });
        }
        if(ClientEventListener_CODE_SHOW_OPTIONS.getRoomHide().compareAndSet(false,true)){

            Platform.runLater(()->{
                RoomHall.roomHallHide();
                gameRoom=new GameRoom();
                gameRoom.init(channel,(int)map.get("roomId"));
                gameRoom.setPlayerName(user.getNickname(), 2);
                gameRoom.setPlayerName("host", 1);
                gameRoom.start(new Stage());
            });
        }
    }
}
