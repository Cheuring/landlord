package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.event.ClientEventListener_CODE_SHOW_OPTIONS;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.PokerUtil;
import io.netty.channel.Channel;
import buaa.oop.landlords.common.enums.ServerEventCode;
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

import java.util.ArrayList;
import java.util.List;

import static buaa.oop.landlords.common.utils.ChannelUtil.pushToClient;
import static buaa.oop.landlords.common.utils.ChannelUtil.pushToServer;

public class GameRoom extends Application {
    private  Channel channel;
    private int roomId;
    private static Label gameStatusLabel = new Label();
    private static VBox player1Cards = new VBox(21);
    private static VBox player3Cards = new VBox(21);
    private static HBox player2Cards = new HBox(21);
    //indexes数组用于记录第i张牌是否被按下
    private static int[] indexes = new int[20];
    private static List<Poker> pokers = new ArrayList<>();   //玩家手牌

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
        HBox actionButtonsBox = new HBox(20);
        actionButtonsBox.setAlignment(Pos.CENTER);
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
            // todo：传个p回去
        });

        // 玩家1的卡牌区（左侧玩家）
        VBox player1Box = new VBox(10);
        player1Box.setAlignment(Pos.CENTER);
        Label player1Name = new Label("玩家1");
        //VBox player1Cards = new VBox(5); // 每张手牌独占一行
        Label card1 = new Label("♠A");
        Label card2 = new Label("♣10");
        Label card3 = new Label("♥K");
        player1Cards.getChildren().addAll(card1, card2, card3);
        player1Box.getChildren().addAll(player1Name, player1Cards);

        // 玩家3的卡牌区（右侧玩家）
        VBox player3Box = new VBox(10);
        player3Box.setAlignment(Pos.CENTER);
        Label player3Name = new Label("玩家3");
        //VBox player3Cards = new VBox(5); // 每张手牌独占一行
        Label card3_1 = new Label("♠K");
        Label card3_2 = new Label("♦2");
        Label card3_3 = new Label("♣Q");
        player3Cards.getChildren().addAll(card3_1, card3_2, card3_3);
        player3Box.getChildren().addAll(player3Name, player3Cards);

        // 玩家2的卡牌区（底部玩家，手牌横向展示）
        VBox player2Box = new VBox(10);
        player2Box.setAlignment(Pos.CENTER);
        Label player2Name = new Label("玩家2");
        //HBox player2Cards = new HBox(5); // 每张手牌横向展示
        Label card2_1 = new Label("♠5");
        Label card2_2 = new Label("♥J");
        Label card2_3 = new Label("♦7");
        player2Cards.getChildren().addAll(card2_1, card2_2, card2_3);
        player2Box.getChildren().addAll(player2Name, player2Cards);

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
    public void init(Channel channel,int romid) {
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

    public static void displayPokers(List<Poker> pokers) {
        player2Cards.getChildren().clear();
        for(int i = 0; i < 20; i++)
            indexes[i] = 0;
        int cnt = pokers.size();
        for(int i = 0; i < cnt; i++) {
            Poker poker = pokers.get(i);
            //拿到牌的大小以及种类，并找到对应路径
            String level = poker.getLevel().getName();
            String type = poker.getType().getName();
            // todo:此处取地址大概率会出错，函数返回文件名
            String imagePath = "/" + getCardImageName(level, type);
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);
            //设置图片大小
            imageView.setFitWidth(50);
            imageView.setFitHeight(70);

            Button cardButton = new Button();
            cardButton.setGraphic(imageView);
            cardButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            int finalI = i;
            cardButton.setOnAction(e -> {
                // todo:在按下按键时让按钮颜色反转
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

    private static String getCardImageName(String level, String type) {
        String code;
        String num = "";
        switch (type) {
            case "♣":
                code = "C";
                break;
            case "♠":
                code = "S";
                break;
            case "♥":
                code = "H";
                break;
            case "♦":
                code = "D";
                break;
            default:
                code = "";
                break;
        }
        switch(level) {
            case "A":
                num = "0";
                break;
            case "2":
                num = "1";
                break;
            case "3":
                num = "2";
                break;
            case "4":
                num = "3";
                break;
            case "5":
                num = "4";
                break;
            case "6":
                num = "5";
                break;
            case "7":
                num = "6";
                break;
            case "8":
                num = "7";
                break;
            case "9":
                num = "8";
                break;
            case "10":
                num = "9";
                break;
            case "J":
                num = "10";
                break;
            case "Q":
                num = "11";
                break;
            case "K":
                num = "12";
                break;
            case "S":
                num = "S";
                break;
            case "B":
                num = "B";
                break;
        }
        return code + num + ".png";
    }
}
