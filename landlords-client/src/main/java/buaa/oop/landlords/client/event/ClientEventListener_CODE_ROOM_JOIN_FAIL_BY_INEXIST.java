package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.GUI.FailPopup;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import javafx.application.Platform;

import java.util.Map;

/**
 * 下一个状态为：ClientEventListener_CODE_SHOW_OPTIONS
 */
public class ClientEventListener_CODE_ROOM_JOIN_FAIL_BY_INEXIST extends ClientEventListener {
    @Override
    public void call(Channel channel, String data) {
        Map<String, Object> room = MapUtil.parse(data);

        SimplePrinter.printNotice("Join room failed. Room " + room.get("roomId") + " don't exist!");
        SimplePrinter.printNotice("");
        ClientEventListener.get(ClientEventCode.CODE_SHOW_OPTIONS).call(channel, data);
        Platform.runLater(()->{
            FailPopup.showPopup("加入房间失败","当前房间人数不存在");
        });
    }
}
