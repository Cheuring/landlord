package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.SimpleClient;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;

import java.util.Map;


/**
 * 根据输入进入
 * 1.ServerEventListener_CODE_ROOM_CREATE
 * 2.ServerEventListener_CODE_ROOM_GETALL
 * 3.ServerEventListener_CODE_ROOM_JOIN
 * 4.ServerEventListener_CODE_CLIENT_OFFLINE
 */
public class ClientEventListener_CODE_SHOW_OPTIONS extends ClientEventListener {

    @Override
    public void call(Channel channel, String data) {
        Map<String, Object> map = MapUtil.parse(data);
        if (map != null && map.containsKey("score")) {
            User.INSTANCE.setScore((Integer) map.get("score"));
            User.INSTANCE.setNickname((String) map.get("username"));
        }

        SimplePrinter.printNotice("Please select the tab you are entering");
        SimplePrinter.printNotice("1.Create Room");
        SimplePrinter.printNotice("2.Get All Rooms");
        SimplePrinter.printNotice("3.Join Room");
        SimplePrinter.printNotice("4.Exit the program");
        SimplePrinter.printNotice("5.Chat ");
        SimplePrinter.printNotice("6.Info");

        String userInput = SimpleWriter.write(User.INSTANCE.getNickname(), "Options");
        if (userInput == null || userInput.isEmpty()) {
            SimplePrinter.printWarning("Please enter a valid option");
            call(channel, null);
            return;
        }

        int userOption;
        try {
            userOption = Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            userOption = -1;
        }
        switch (userOption) {
            case 1:
                pushToServer(channel, ServerEventCode.CODE_ROOM_CREATE, null);
                break;
            case 2:
                pushToServer(channel, ServerEventCode.CODE_ROOM_GETALL, null);
                break;
            case 3:
                joinRoom(channel);
                break;
            case 4:
                channel.close();
                break;
            case 5:
                SimplePrinter.printNotice("Please enter your content in such format\n@[ClientToName] [Content]");
                SimpleClient.chatRoom.start(ClientEventCode.CODE_SHOW_OPTIONS);
                break;
            case 6:
                SimplePrinter.printPrompt("username:\t%s\nscore:\t%d\n".formatted(User.INSTANCE.getNickname(), User.INSTANCE.getScore()));
                call(channel, null);
                break;
            default:
                SimplePrinter.printNotice("Invalid option, please choose again:");
                call(channel, null);
        }

    }

    public void joinRoom(Channel channel) {
        SimplePrinter.printNotice("Please enter the room id you want to join (enter [back|b] return options list)");
        String roomId = SimpleWriter.write();
        if (roomId == null) {
            SimplePrinter.printNotice("Please enter a valid room id");
            joinRoom(channel);
            return;
        }
        if (roomId.equalsIgnoreCase("BACK") || roomId.equalsIgnoreCase("b")) {
            call(channel, null);
            return;
        }

        int userOption;
        try {
            userOption = Integer.parseInt(roomId);
        } catch (NumberFormatException e) {
            userOption = -1;
        }
        if (userOption < 1) {
            SimplePrinter.printNotice("Please enter a valid room id");
            joinRoom(channel);
        }
        pushToServer(channel, ServerEventCode.CODE_ROOM_JOIN, String.valueOf(userOption));
    }

}
