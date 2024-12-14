package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.GUI.GameRoom;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import javafx.application.Platform;

import java.util.Map;

/**
 *如果退出：下一个状态ServerEventListener_CODE_CLIENT_EXIT
 *如果选择分数：下一个状态为ServerEventListener_CODE_GAME_LANDLORD_ELECT,发送参数为 score分数
 */
public class ClientEventListener_CODE_GAME_LANDLORD_ELECT extends ClientEventListener{
    @Override
    /**
     * @param data includes room info,last and next player,yourself pokers and highscores(for elect landlord)
     */
    public void call(Channel channel, String data) {
        Map<String, Object> roomInfo= MapUtil.parse(data);
        int nextClientId = (int) roomInfo.get("nextClientId");
        int highestScore = (int) roomInfo.get("highestScore");
        if (roomInfo.containsKey("preClientNickname")) {
            // todo: 更新前面的玩家的分数 以及是否抢地主
            if(highestScore !=0&&roomInfo.get("preClientId") == roomInfo.get("currentLandlordId")){
                SimplePrinter.printNotice(roomInfo.get("preClientNickname")+" robs the landlord with " + highestScore+" scores.");
            }
            else SimplePrinter.printNotice(roomInfo.get("preClientNickname") + " don't rob the landlord!");
        }

        if(nextClientId != User.getINSTANCE().getId()){
            SimplePrinter.printNotice("It's not your turn. Please wait patiently for his/her confirmation !");
            SimplePrinter.printNotice("");
            return;
        }

        String s="It's  your turn.Please select your score [0";
        for(int i = highestScore + 1; i <= 3; ++i) {
            s = s + "/" + i;
        }
        s = s + "] (enter [exit|e] to exit current room)";
        SimplePrinter.printNotice(s);

        Platform.runLater(() -> {
            GameRoom.electButtonOn(highestScore, (Integer) roomInfo.get("currentLandlordId"));
        });
    }
}

