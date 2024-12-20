package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.SimpleClient;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import io.netty.channel.Channel;
import buaa.oop.landlords.common.print.*;
import io.netty.util.concurrent.Future;


/**
 * 根据输入进入
 *   1.ServerEventListener_CODE_ROOM_CREATE
 *   2.ServerEventListener_CODE_ROOM_GETALL
 *   3.ServerEventListener_CODE_ROOM_JOIN
 *   4.ServerEventListener_CODE_CLIENT_OFFLINE
 */
public class ClientEventListener_CODE_SHOW_OPTIONS extends ClientEventListener {

    @Override
    /**
     * @param data is always null or no use
     */
    public void call(Channel channel, String data) {

//        System.exit(0);
        SimplePrinter.printNotice("Please select the tab you are entering");
        SimplePrinter.printNotice("1.Create Room");
        SimplePrinter.printNotice("2.Get All Rooms");
        SimplePrinter.printNotice("3.Join Room");
        SimplePrinter.printNotice("4.Exit the program");
        SimplePrinter.printNotice("5.Chat ");

        String userInput = SimpleWriter.write(User.INSTANCE.getNickname(), "Options");
        if(userInput == null || userInput.isEmpty()){
            SimplePrinter.printNotice("Please enter a valid option");
            call(channel,data);
            return;
        }

        int userOption;
        try {
            userOption= Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            userOption=-1;
        }
        switch(userOption){
            case 1:
                pushToServer(channel, ServerEventCode.CODE_ROOM_CREATE,null);
                break;
            case 2:
                pushToServer(channel, ServerEventCode.CODE_ROOM_GETALL,null);
                break;
            case 3:
                joinRoom(channel,data);
                break;
            case 4:
                channel.close();
                break;
            case 5:
                SimplePrinter.printNotice("Please enter your content in such format\n@[ClientToName] [Content]");
                SimpleClient.chatRoom.start(ClientEventCode.CODE_SHOW_OPTIONS);
                break;
            default:
                SimplePrinter.printNotice("Invalid option, please choose again:");
                call(channel, data);
        }

    }
    public void joinRoom(Channel channel,String data){
        SimplePrinter.printNotice("Please enter the room id you want to join (enter [back|b] return options list)");
        String roomId= SimpleWriter.write();
        if(roomId==null){
            SimplePrinter.printNotice("Please enter a valid room id");
            joinRoom(channel,data);
            return;
        }
        if(roomId.equalsIgnoreCase("BACK") || roomId.equalsIgnoreCase("b")){
            call(channel,data);
            return;
        }

        int userOption;
        try {
            userOption= Integer.parseInt(roomId);
        } catch (NumberFormatException e) {
            userOption=-1;
        }
        if(userOption<1){
            SimplePrinter.printNotice("Please enter a valid room id");
            joinRoom(channel,data);
        }
        pushToServer(channel,ServerEventCode.CODE_ROOM_JOIN, String.valueOf(userOption));
    }

}
