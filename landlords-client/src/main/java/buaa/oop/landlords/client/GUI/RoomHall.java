package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.*;

import static buaa.oop.landlords.common.utils.ChannelUtil.pushToServer;

public class RoomHall extends Application {
    private static final int ROOMS_PER_ROW = 3; // 每行房间数
    private static List<Map<String, Object>> roomList=null;
    private static final int ROOMS_MAX=150;

    private static Channel channel;

    private static TextArea chatArea = new TextArea();
    private static Stage primaryStage=new Stage();
    private static GridPane roomDisplayArea = new GridPane();
    private static BorderPane mainLayout = new BorderPane();
    private static ScrollPane scrollPane = new ScrollPane();

    private static TextField inputField = new TextField();
    private static int idx;

    @Override
    public void start(Stage Stage) {
    }

    private static void updateRoomDisplay(GridPane roomDisplayArea) {
        roomDisplayArea.getChildren().clear();
        int row = 0;
        int col = 0;
        if (roomList != null) {
            // 先显示有人的房间
            for (int i = 0; i < roomList.size(); i++) {
                Map<String, Object> room = roomList.get(i);

                String roomId = Integer.toString((Integer) room.get("roomId"));
                String roomOwner = (String) room.get("roomOwner");
                String roomStatus = JsonUtil.fromJson((String) room.get("roomStatus"), String.class);
                int roomClientCount = (int) room.get("roomClientCount");

                VBox roomLayout = new VBox(10);
                roomLayout.setPadding(new Insets(10));
                roomLayout.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");

                // 房间按钮
                Button roomButton = new Button("房间 " + roomId);
                roomButton.setPrefWidth(180);
                roomButton.setPrefHeight(60);
                roomButton.setOnAction(e -> {
                    pushToServer(channel,ServerEventCode.CODE_ROOM_JOIN,roomId);
                });

                Tooltip roomTooltip = new Tooltip(
                        "房主: " + roomOwner + "\n" +
                                "状态: " + roomStatus + "\n" +
                                "人数: " + roomClientCount
                );
                Tooltip.install(roomButton, roomTooltip);

                roomLayout.getChildren().addAll(roomButton);

                roomDisplayArea.add(roomLayout, col, row);
                col++;
                if (col >= ROOMS_PER_ROW) {
                    col = 0;
                    row++;
                }
            }

        }
        int i;
        if(roomList==null) i=0;
        else i=roomList.size();
        // 添加无人的房间
        for (; i < ROOMS_MAX; i++) {
            VBox roomLayout = new VBox(10);
            roomLayout.setPadding(new Insets(10));
            roomLayout.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");

            // 空房间按钮
            Button emptyRoomButton = new Button("房间 " + (i + 1));
            emptyRoomButton.setPrefWidth(180);
            emptyRoomButton.setPrefHeight(60);

            // 创建房间按钮
            Button createRoomButton = new Button("+");
            createRoomButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16;");
            createRoomButton.setOnMouseEntered(e -> {
                createRoomButton.setText("创建房间");
            });
            createRoomButton.setOnMouseExited(e -> {
                createRoomButton.setText("+");
            });
            createRoomButton.setOnAction(e -> pushToServer(channel, ServerEventCode.CODE_ROOM_CREATE,null));

            roomLayout.getChildren().addAll(emptyRoomButton, createRoomButton);

            roomDisplayArea.add(roomLayout, col, row);

            col++;
            if (col >= ROOMS_PER_ROW) {
                col = 0;
                row++;
            }
        }
    }


    public static void setRoomList(List<Map<String, Object>> roomList1) {
        roomList = roomList1;
        updateRoomDisplay(roomDisplayArea);
    }
    public static void stageInit(Channel channelt){
        channel=channelt;
        primaryStage.setTitle("游戏大厅");

        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        roomDisplayArea.setHgap(10);
        roomDisplayArea.setVgap(10);
        roomDisplayArea.setAlignment(Pos.TOP_CENTER);

        scrollPane.setContent(roomDisplayArea);

        mainLayout.setCenter(scrollPane);
        updateRoomDisplay(roomDisplayArea);

        // 设置主布局
        HBox hbox = new HBox();
        hbox.setSpacing(10); // 控件间距
        hbox.setAlignment(Pos.CENTER_LEFT);

        // 将左侧的内容区域 (房间显示区域) 设置为四分之三宽度
        Region leftRegion = new Region();
        HBox.setHgrow(leftRegion, Priority.ALWAYS);
        leftRegion.setMinWidth(0); // 设置最小宽度为 0，避免拉伸到最大宽度

        // 右侧的聊天框区域（四分之一宽度）
        VBox chatBox = new VBox();
        chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");

        // 上部显示聊天信息

        chatArea.setEditable(false); // 禁止编辑聊天记录
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(300);

        // 下方输入框
        inputField.setPromptText("请输入消息...");
        Button sendButton = new Button("发送");

        // 发送按钮事件
        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()&&isVaild(message)) {
                chatArea.appendText(User.getINSTANCE().getNickname()+": " + message + "\n");
                inputField.clear();
                String result = MapUtil.newInstance()
                        .put("ClientTo",message.substring(1, idx))
                        .put("Content", message.substring(idx + 1))
                        .json();
                pushToServer(channel,ServerEventCode.CODE_CHAT,result);
            }else {
                inputField.setStyle("-fx-border-color: red; -fx-font-size: 14px;");
                inputField.clear();
            }
        });

        Label formatHint = new Label("Please enter your content in such format:\n@[ClientToName] [Content]");
        formatHint.setStyle("-fx-font-size: 12; -fx-text-fill: gray; -fx-padding: 5;");
        chatBox.getChildren().addAll(chatArea, inputField, sendButton, formatHint);

        hbox.getChildren().addAll(leftRegion, scrollPane, chatBox);

        mainLayout.setCenter(hbox);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
    }
    public static void roomHallDisplay(){
        primaryStage.show();
    }
    public static void roomHallHide(){
        primaryStage.hide();
    }
    public static void msgDisplay(String msg){
        chatArea.appendText(msg);
    }
    private static boolean isVaild(String msg){
        if(msg.charAt(0) == '@'&&(idx=msg.indexOf(' '))>1){
            return true;
        }return false;
    }
}