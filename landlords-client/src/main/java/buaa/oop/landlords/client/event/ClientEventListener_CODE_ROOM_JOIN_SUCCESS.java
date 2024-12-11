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

        int joinClientId = (int) map.get("clientId");
        if (User.getINSTANCE().getId() == joinClientId) {
            SimplePrinter.printNotice("You have joined room：" + map.get("roomId") + ". There are " + map.get("roomClientCount") + " players in the room now.");
            SimplePrinter.printNotice("Please wait for other players to join. The game would start at three players!");
        } else {
            SimplePrinter.printNotice(map.get("clientNickname") + " joined room, there are currently " + map.get("roomClientCount") + " in the room.");
        }
        if(ClientEventListener_CODE_SHOW_OPTIONS.getRoomHide().compareAndSet(false,true)){

            Platform.runLater(()->{
                RoomHall.roomHallHide();
                GameRoom gameRoom=new GameRoom();
                gameRoom.setChannel(channel);
                gameRoom.start(new Stage());
            });
        }
    }
}
