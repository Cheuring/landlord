package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.GUI.Login;
import buaa.oop.landlords.client.GUI.RoomHall;
import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.SimpleClient;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import buaa.oop.landlords.common.print.*;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 根据输入进入
 * 1.ServerEventListener_CODE_ROOM_CREATE
 * 2.ServerEventListener_CODE_ROOM_GETALL
 * 3.ServerEventListener_CODE_ROOM_JOIN
 * 4.ServerEventListener_CODE_CLIENT_OFFLINE
 */
public class ClientEventListener_CODE_SHOW_OPTIONS extends ClientEventListener {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicBoolean schedulerStarted = new AtomicBoolean(false);
    private final AtomicBoolean roomDisplay = new AtomicBoolean(false);
    private static final AtomicBoolean roomHide= new AtomicBoolean(false);

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
//        SimpleClient.chatRoom.start(ClientEventCode.CODE_CHAT);
        if (User.getINSTANCE().getScore() == 0) {
            try {
                User.getINSTANCE().setScore((int) (MapUtil.parse(data).get("score")));
            } catch (Exception e) {
                System.out.println("data is null");
            }
        }
        if (roomDisplay.compareAndSet(false, true)) {
            Platform.runLater(() -> {
                try{
                    Stage stage = Login.getPrimaryStage();
                    GUIUtil.autoCloseAlertHandler(stage);
                    stage.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                RoomHall.stageInit(channel);
                RoomHall.roomHallDisplay();
            });
        }
        if(roomHide.compareAndSet(true, false)) {
            Platform.runLater(() -> {
                RoomHall.updataScoreArea();
                RoomHall.roomHallDisplay();
            });
        }
        if (schedulerStarted.compareAndSet(false, true)) {
            startRoomListScheduler(channel);
        }
    }

    public void joinRoom(Channel channel, String data) {
        SimplePrinter.printNotice("Please enter the room id you want to join (enter [back|b] return options list)");
        String roomId = SimpleWriter.write();
        if (roomId == null) {
            SimplePrinter.printNotice("Please enter a valid room id");
            joinRoom(channel, data);
            return;
        }
        if (roomId.equalsIgnoreCase("BACK") || roomId.equalsIgnoreCase("b")) {
            call(channel, data);
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
            joinRoom(channel, data);
        }
        pushToServer(channel, ServerEventCode.CODE_ROOM_JOIN, String.valueOf(userOption));
    }

    private void startRoomListScheduler(Channel channel) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                pushToServer(channel, ServerEventCode.CODE_ROOM_GETALL, null);
                SimplePrinter.printNotice("Room list fetched from the server.");
            } catch (Exception e) {
                SimplePrinter.printNotice("Error fetching room list: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void stopRoomListScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        SimplePrinter.printNotice("Room list scheduler stopped.");
    }
    public static AtomicBoolean getRoomHide() {
        return roomHide;
    }

    public AtomicBoolean getRoomDisplay() {
        return roomDisplay;
    }
}
