package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;

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
            if(highestScore !=0&&roomInfo.get("preClientId") == roomInfo.get("currentLandlordId")){
                SimplePrinter.printNotice(roomInfo.get("preClientNickname")+" robs the landlord with " + highestScore+" scores ");
            }
            else SimplePrinter.printNotice(roomInfo.get("preClientNickname") + " don't rob the landlord!");
        }
        if(nextClientId != User.getINSTANCE().getId()){
            SimplePrinter.printNotice("It's not your turn. Please wait patiently for his/her confirmation !");
        }
        else{
            String s="It's  your turn.Please select your score [0";
            for(int i = highestScore + 1; i <= 3; ++i) {
                s = s + "/" + i;
            }
            s = s + "] (enter [exit|e] to exit current room)";
            SimplePrinter.printNotice(s);
            String userInput = SimpleWriter.write(User.getINSTANCE().getNickname(), "getScore");
            if (userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("e")) {
                pushToServer(channel, ServerEventCode.CODE_CLIENT_EXIT);
            } else {
                int currentScore;
                try {
                    currentScore = Integer.parseInt(userInput);
                } catch (Exception e) {
                    SimplePrinter.printNotice("Invalid options");
                    currentScore = -1;
                    this.call(channel, data);
                }
                if (currentScore <= highestScore && currentScore != 0 || currentScore > 3) {
                    SimplePrinter.printNotice("Invalid options");
                    this.call(channel, data);
                    return;
                }
                String result;
                if (currentScore > highestScore) {
                    result = MapUtil.newInstance()
                            .put("highestScore", currentScore)
                            .put("currentLandlordId", User.getINSTANCE().getId())
                            .json();
                } else if (roomInfo.containsKey("currentLandlordId")) {
                    result = MapUtil.newInstance()
                            .put("highestScore", highestScore)
                            .put("currentLandlordId", (int) roomInfo.get("currentLandlordId"))
                            .json();
                } else {
                    result = MapUtil.newInstance()
                            .put("highestScore", 0)
                            .json();
                }

                this.pushToServer(channel, ServerEventCode.CODE_GAME_LANDLORD_ELECT, result);
            }
        }
    }
}

