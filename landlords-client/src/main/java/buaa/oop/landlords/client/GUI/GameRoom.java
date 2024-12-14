package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.ClientContainer;
import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.client.event.ClientEventListener_CODE_SHOW_OPTIONS;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.PokerLevel;
import buaa.oop.landlords.common.enums.PokerType;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.common.utils.PokerUtil;
import io.netty.channel.Channel;
import buaa.oop.landlords.common.enums.ServerEventCode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static buaa.oop.landlords.common.utils.ChannelUtil.pushToClient;
import static buaa.oop.landlords.common.utils.ChannelUtil.pushToServer;

public class GameRoom extends Application {
    private  Channel channel;
    private int roomId;
    private static Label gameStatusLabel = new Label();
    private static HBox actionButtonsBox = new HBox(20);
    private static VBox player1Cards = new VBox(-50);
    private static VBox player3Cards = new VBox(-50);
    private static HBox player2Cards = new HBox(-50);
    //indexes数组用于记录第i张牌是否被按下
    private static int[] indexes = new int[20];
    private static List<Poker> pokers = new ArrayList<>();   //玩家手牌

    private static VBox p1LastPokers = new VBox(-50);
    private static VBox p3LastPokers = new VBox(-50);


    private static final Object gameRoomLock = new Object();
    public static boolean isElect=false;
    public static String robScore="";
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("三人斗地主房间");

        // 房间信息展示区
        VBox roomInfoBox = new VBox(10);
        roomInfoBox.setAlignment(Pos.TOP_CENTER);
        Label roomInfoLabel = new Label("房间号: "+Integer.toString(roomId));
        gameStatusLabel.setText("游戏状态: 等待其他玩家加入");
        roomInfoBox.getChildren().addAll(roomInfoLabel, gameStatusLabel);
//        Label gameStatusLabel = new Label("游戏状态: 等待其他玩家准备");
//        roomInfoBox.getChildren().addAll(roomInfoLabel, gameStatusLabel);

        // 退出按钮

        Button exitButton = new Button("返回大厅");
        exitButton.setOnAction(e -> {
            System.out.println("玩家退出房间");
            primaryStage.close();
            pushToServer(channel, ServerEventCode.CODE_ROOM_EXIT,null);
            if(ClientEventListener_CODE_SHOW_OPTIONS.getRoomHide().compareAndSet(true,false)){
                RoomHall.roomHallDisplay();
            }
        });

        // 顶部布局，包含房间信息和退出按钮
        HBox topLayout = new HBox();
        topLayout.setAlignment(Pos.CENTER);
        topLayout.setPadding(new Insets(10));
        topLayout.getChildren().addAll(roomInfoBox, exitButton);
        HBox.setHgrow(roomInfoBox, Priority.ALWAYS);
        topLayout.setSpacing(10);

        // 操作按钮区，水平排列
        actionButtonsBox.setAlignment(Pos.CENTER);
        /*
        Button playButton = new Button("出牌");
        playButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 10;");
        Button passButton = new Button("过牌");
        passButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 10;");
        actionButtonsBox.getChildren().addAll(playButton, passButton);

        playButton.setOnAction(e -> {
            List<Poker> selectedCards = getSelectedCards(indexes, pokers);
            if(!selectedCards.isEmpty()){
                // todo:传回POKERPLAY进行进一步判断

            }
            else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("警告");
                alert.setHeaderText(null);
                alert.setContentText("请选择要出的卡牌");
                alert.showAndWait();
            }
        });

        passButton.setOnAction(e -> {

        });
         */

        // 玩家1的卡牌区（左侧玩家）
        VBox player1Box = new VBox(10);
        player1Box.setAlignment(Pos.CENTER);
        Label player1Role = new Label("农民");
        Label player1Name = new Label("玩家1");
        player1Box.getChildren().addAll(player1Role, player1Name, player1Cards);

        VBox p1LastPokers = new VBox(10);

        // 玩家3的卡牌区（右侧玩家）
        VBox player3Box = new VBox(10);
        player3Box.setAlignment(Pos.CENTER);
        Label player3Role = new Label("农民");
        Label player3Name = new Label("玩家3");
        player3Box.getChildren().addAll(player3Role, player3Name, player3Cards);

        // 玩家2的卡牌区（底部玩家，手牌横向展示）
        VBox player2Box = new VBox(10);
        player2Box.setAlignment(Pos.CENTER);
        Label player2Role = new Label("农民");
        Label player2Name = new Label("玩家2");
        player2Cards.setAlignment(Pos.CENTER);
        player2Box.getChildren().addAll(player2Role, player2Name, player2Cards);

        // 左右玩家布局
        BorderPane sidePlayersLayout = new BorderPane();
        sidePlayersLayout.setLeft(player1Box);
        sidePlayersLayout.setRight(player3Box);

        // 主布局（回字结构）
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topLayout);
        mainLayout.setCenter(actionButtonsBox);
        mainLayout.setBottom(player2Box);
        mainLayout.setLeft(player1Box);
        mainLayout.setRight(player3Box);

        // 设置场景
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        GUIUtil.cancelHandler(primaryStage);
        primaryStage.show();
    }

    public void init(Channel channel, int romid) {
        this.channel = channel;
        this.roomId = romid;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setRoomStatus(String title) {
        gameStatusLabel.setText(title);
    }

    private static List<Poker> getSelectedCards(int[] index, List<Poker> pokers) {
        List<Poker> selectedCards = new ArrayList<>();
        int[] list = new int[20];
        int pos = 0;
        for(int i = 0; i < 20; i++) {
             if(index[i] == 1)
                 list[pos++] = i;
         }
         //此处由于getPoker()方法的原因，只能再开个数组
         int[] ans = new int[list.length];
         for (int i = 0; i < list.length; i++)
             ans[i] = list[i];
         selectedCards = PokerUtil.getPoker(ans, pokers);
         return selectedCards;
    }

    //更新自己手牌
    public static void displayPokers(List<Poker> pokers) {
        player2Cards.getChildren().clear();
        for(int i = 0; i < 20; i++)
            indexes[i] = 0;
        int cnt = pokers.size();
        for(int i = 0; i < cnt; i++) {
            Poker poker = pokers.get(i);
            int idx = poker.getLevel().getIdx();
            int value = poker.getType().getValue();
            ImageView imageView = GUIUtil.getPokerImage(idx, value);

            Button cardButton = new Button();
            cardButton.setGraphic(imageView);
            cardButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            int finalI = i;
            cardButton.setOnAction(e -> {
                // todo:在按下按键时让按钮颜色反转
                System.out.println("1");
                if(indexes[finalI] == 0) {
                    indexes[finalI] = 1;
                }
                else {
                    indexes[finalI] = 0;
                }
            });
            player2Cards.getChildren().add(cardButton);
        }
    }

    //更新他人手牌
    public static void updatePokers(int size, int player) {
        ImageView imageView = GUIUtil.getPokerBackImage();
        if(player == 1) {
            for(int i = 0; i < size; i++)
                player1Cards.getChildren().add(imageView);
        }
        else {
            for(int i = 0; i < size; i++)
                player1Cards.getChildren().add(imageView);
        }
    }

    public static void electButtonOn(int point, Integer currentLandlordId) {
        Button[] buttons = new Button[4];

        buttons[0] = new Button("0");
        buttons[0].setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 5;");
        buttons[0].setOnAction(e -> {
            String result;
             if (currentLandlordId != null) {
                result = MapUtil.newInstance()
                        .put("highestScore", point)
                        .put("currentLandlordId", currentLandlordId)
                        .json();
            } else {
                result = MapUtil.newInstance()
                        .put("highestScore", 0)
                        .json();
            }
            pushToServer(ClientContainer.channel, ServerEventCode.CODE_GAME_LANDLORD_ELECT, result);
        });

        for(int i=1; i<4; ++i){
            int finalI = i;
            buttons[i] = new Button(Integer.toString(i));
            if(i <= point){
                buttons[i].setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 5;");
                buttons[i].setOnAction(e -> {
                    buttons[finalI].setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 5; -fx-border-color: red; -fx-border-width: 1;");

                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                        buttons[finalI].setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 5;");
                    }));

                    timeline.play();
                });
            }else{
                buttons[i].setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 5;");
                buttons[i].setOnAction(e -> {
                    String result = MapUtil.newInstance()
                            .put("highestScore", finalI)
                            .put("currentLandlordId", User.getINSTANCE().getId())
                            .json();
                    actionButtonsBox.getChildren().clear();
                    pushToServer(ClientContainer.channel, ServerEventCode.CODE_GAME_LANDLORD_ELECT, result);
                });
            }
        }

        actionButtonsBox.getChildren().addAll(buttons);
    }

}
