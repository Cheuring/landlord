package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import jdk.jshell.ImportSnippet;

import java.util.*;

import static buaa.oop.landlords.common.utils.ChannelUtil.pushToServer;

public class RoomHall extends Application {
    private static final int ROOMS_PER_ROW = 3; // 每行房间数
    private static List<Map<String, Object>> roomList=null;
    private static final int ROOMS_MAX=150;

    private static Channel channel;

    private static TextFlow chatArea = new TextFlow();
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
                roomButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16;");
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
            Button emptyRoomButton = new Button("空房间" );
            emptyRoomButton.setPrefWidth(180);
            emptyRoomButton.setPrefHeight(60);
            emptyRoomButton.setStyle("-fx-background-color: grey; -fx-text-fill: white; -fx-font-size: 16;");
            emptyRoomButton.setOnMouseEntered(e -> {
                emptyRoomButton.setText("创建房间");
            });

            emptyRoomButton.setOnAction(e -> pushToServer(channel, ServerEventCode.CODE_ROOM_CREATE,null));

            roomLayout.getChildren().addAll(emptyRoomButton);

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
    public static void stageInit(Channel channel){
        RoomHall.channel = channel;
        primaryStage.setTitle("游戏大厅");

        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));
        roomDisplayArea.setHgap(10);
        roomDisplayArea.setVgap(10);
        roomDisplayArea.setAlignment(Pos.TOP_CENTER);
        scrollPane.setContent(roomDisplayArea);
        mainLayout.setCenter(scrollPane);
        updateRoomDisplay(roomDisplayArea);

        HBox hbox = new HBox();
       // hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Region leftRegion = new Region();
        HBox.setHgrow(leftRegion, Priority.ALWAYS);
        leftRegion.setMinWidth(0);

        //彩蛋区域
        VBox hiddenLeftBox = new VBox();
        hiddenLeftBox.setPadding(new Insets(10));
        Image hiddenEgg=new Image("/images/eggs1.gif");
        ImageView hiddenEggView = new ImageView(hiddenEgg);
        hiddenEggView.setFitHeight(500);
        hiddenEggView.setPreserveRatio(true);
        hiddenLeftBox.getChildren().add(hiddenEggView);
        hiddenLeftBox.setMinWidth(0);
        hiddenLeftBox.setMaxWidth(0);

        //聊天区域
        VBox chatBox = new VBox();
        //chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
        chatArea.setStyle("-fx-background-color: white; -fx-padding: 10;");
        chatArea.setPrefHeight(320);

        inputField.setPromptText("请输入消息...");
        inputField.setOnKeyPressed(e -> {
            if (e.getCode().getName().equals("Enter")) {
                sendMessage();
            }
        });
        Button sendButton = new Button("发送");
        sendButton.setOnAction(e -> sendMessage());
        Label formatHint = new Label("Format:@[ClientToName] [Content]");
        formatHint.setStyle("-fx-font-size: 12; -fx-text-fill: gray; -fx-padding: 5;");
        chatBox.getChildren().addAll(chatArea, inputField, sendButton, formatHint);

        VBox rightBottomBox = new VBox();
        rightBottomBox.setSpacing(10);
        rightBottomBox.setPadding(new Insets(10));
        rightBottomBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");

        Label nameLabel = new Label("姓名: ");
        TextArea nameArea = new TextArea();
        nameArea.appendText(User.INSTANCE.getNickname());
        nameArea.setEditable(false);
        nameArea.setPrefHeight(50);
        nameArea.setStyle("-fx-background-color: white; -fx-padding: 5;");

        Label scoreLabel = new Label("分数: ");
        TextArea scoreArea = new TextArea();
        scoreArea.appendText(String.valueOf(User.INSTANCE.getScore()));
        scoreArea.setEditable(false);
        scoreArea.setPrefHeight(50);
        scoreArea.setStyle("-fx-background-color: white; -fx-padding: 5;");
        rightBottomBox.setPrefWidth(20);
        rightBottomBox.getChildren().addAll(nameLabel, nameArea, scoreLabel, scoreArea);

        VBox rightTopBox = new VBox();
        Image gifImage = new Image("/images/landlordPhoto.gif");
        ImageView imageView = new ImageView(gifImage);

        imageView.setFitWidth(150);
        imageView.setFitHeight(230);
        imageView.setPreserveRatio(true);

        rightTopBox.getChildren().add(imageView);

        HBox rightBox = new HBox();
        rightBox.setSpacing(10);
        rightBox.setPadding(new Insets(10));

        rightBox.getChildren().addAll(rightBottomBox, rightTopBox);

        VBox right = new VBox();
       // right.setSpacing(10);
        right.getChildren().addAll(chatBox, rightBox);

        HBox mainBox = new HBox();
       // mainBox.setSpacing(10);
        mainBox.getChildren().addAll(leftRegion, scrollPane, right);
        mainBox.getChildren().add(0, hiddenLeftBox);
        mainLayout.setCenter(mainBox);

        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
    }

    private static void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()&& isValid(message)) {
//                chatArea.appendText(User.getINSTANCE().getNickname()+": " + message + "\n");
            inputField.clear();

            String clientTo = message.substring(1, idx);
            if(clientTo.equals(User.INSTANCE.getNickname())){
                inputField.setStyle("-fx-border-color: red; -fx-font-size: 14px;");
                inputField.setPromptText("不能私聊自己");
                return;
            }

            String result = MapUtil.newInstance()
                    .put("ClientTo", clientTo)
                    .put("Content", message.substring(idx + 1))
                    .json();
            pushToServer(channel,ServerEventCode.CODE_CHAT,result);
            inputField.setPromptText("请输入消息...");
            inputField.setStyle("");
        }else {
            inputField.setStyle("-fx-border-color: red; -fx-font-size: 14px;");
            inputField.setPromptText("请输入正确格式");
            inputField.clear();
        }
    }

    public static void roomHallDisplay() {
        GUIUtil.cancelHandler(primaryStage);
        primaryStage.show();
    }
    public static void roomHallHide(){
        GUIUtil.autoCloseAlertHandler(primaryStage);
        primaryStage.hide();
    }
    public static void msgDisplay(String msg,String to){
        Platform.runLater(()->{
            Text text=new Text(msg);
            if(!to.equalsIgnoreCase("all"))text.setStyle("-fx-fill: pink; -fx-font-size: 14px; -fx-font-weight: bold;");
            else text.setStyle("-fx-fill: black; -fx-font-size: 14px; -fx-font-weight: bold;");
            chatArea.getChildren().add(text);});

    }
    private static boolean isValid(String msg){
        if(msg.charAt(0) == '@'&&(idx=msg.indexOf(' '))>1){
            return true;
        }return false;
    }
}