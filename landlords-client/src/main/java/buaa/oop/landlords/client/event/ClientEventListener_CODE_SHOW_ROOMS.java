package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.GUI.RoomHall;
import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;

/**
 * 解析data为房间信息的List<Map<String, Object>>并打印
 * 下一个状态：ClientEventListener_CODE_SHOW_OPTIONS
 */
public class ClientEventListener_CODE_SHOW_ROOMS extends ClientEventListener {
    @Override
    /**
     * @param data is Json ,original type is list<Map<String, Object>>
     */
    public void call(Channel channel, String data) {
        List<Map<String, Object>> roomList = JsonUtil.fromJson(data, new TypeReference<List<Map<String, Object>>>() {
        });
       Platform.runLater(()->{RoomHall.setRoomList(roomList);});
        if (roomList != null && !roomList.isEmpty()) {
            String format = "#\t%s\t|\t%-" + 16 + "s\t|\t%-8s\t|\t%-14s|#\n";

            System.out.printf(format, "ID", "OWNER", "STATUS", "CLIENTCOUNT");
            for (Map<String, Object> room : roomList) {
                System.out.printf(format, room.get("roomId"), room.get("roomOwner"), JsonUtil.fromJson((String) room.get("roomStatus"), String.class), room.get("roomClientCount"));
            }
            SimplePrinter.printNotice("");
            get(ClientEventCode.CODE_SHOW_OPTIONS).call(channel, data);
        } else {
            SimplePrinter.printNotice("No available room. Please create a room!");
            get(ClientEventCode.CODE_SHOW_OPTIONS).call(channel, data);
        }

    }
}
