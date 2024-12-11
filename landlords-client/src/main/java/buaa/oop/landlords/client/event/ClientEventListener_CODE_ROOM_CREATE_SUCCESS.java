package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.GUI.GameRoom;
import buaa.oop.landlords.client.GUI.RoomHall;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import io.netty.channel.Channel;
import buaa.oop.landlords.client.event.ClientEventListener_CODE_SHOW_OPTIONS;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * 参数为新创建的room
 * 无下一个状态
 */
public class ClientEventListener_CODE_ROOM_CREATE_SUCCESS extends ClientEventListener{
    @Override
    /**
     * @param data is string of room id
     */
    public void call(Channel channel, String data) {
//        Room room = JsonUtil.fromJson(data, Room.class);
        int roomId = Integer.parseInt(data);
        SimplePrinter.printNotice("You have created a room with id " + roomId);
        SimplePrinter.printNotice("Please wait for other players to join !");

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
