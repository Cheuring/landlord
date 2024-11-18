package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.ChatRoom;
import buaa.oop.landlords.client.SimpleClient;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import io.netty.channel.Channel;
import buaa.oop.landlords.common.print.*;


/**
 * 根据输入进入
 *   1.ServerEventListener_CODE_ROOM_CREATE
 *   2.ServerEventListener_CODE_ROOM_GETALL
 *   3.ServerEventListener_CODE_ROOM_JOIN
 *   4.ServerEventListener_CODE_CLIENT_OFFLINE
 */
public class ClientEventListener_CODE_SHOW_OPTIONS extends ClientEventListener {
    private final Object lock = new Object();
    @Override
    /**
     * @param data is always null or no use
     */
    public void call(Channel channel, String data) {
        SimpleClient.chatRoom = new ChatRoom(channel, lock);
//        System.exit(0);
        SimplePrinter.printNotice("Please select the tab you are entering");
        SimplePrinter.printNotice("1.Create Room");
        SimplePrinter.printNotice("2.Get All Rooms");
        SimplePrinter.printNotice("3.Join Room");
        SimplePrinter.printNotice("4.Exit the program");
        SimplePrinter.printNotice("5.Chat ");

        String userInput= SimpleWriter.write();
        if(userInput==null){
            SimplePrinter.printNotice("Please enter a valid option");
            call(channel,data);
        }
        else{
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
                    pushToServer(channel,ServerEventCode.CODE_CLIENT_OFFLINE,null);
                    break;
                case 5:
                    SimplePrinter.printNotice("Please enter your content in such format");
                    SimplePrinter.printNotice("@[yourChatterId] [chatinformation]");

                    synchronized (lock) {
                        SimpleClient.chatRoom.start();
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    SimplePrinter.printNotice("Invalid option, please choose again：");
                    call(channel, data);
            }
        }
    }
    public void joinRoom(Channel channel,String data){
        SimplePrinter.printNotice("Please enter the room id you want to join (enter [back|b] return options list)");
        String roomId= SimpleWriter.write();
        if(roomId==null){
            SimplePrinter.printNotice("Please enter a valid room id");
            joinRoom(channel,data);
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
