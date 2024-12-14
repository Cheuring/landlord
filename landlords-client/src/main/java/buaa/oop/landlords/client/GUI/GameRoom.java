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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("三人斗地主房间");

        // 房间信息展示区
        VBox roomInfoBox = new VBox(10);
        roomInfoBox.setAlignment(Pos.TOP_CENTER);
        Label roomInfoLabel = new Label("房间号: 1234");
        Label gameStatusLabel = new Label("游戏状态: 等待其他玩家准备");
        roomInfoBox.getChildren().addAll(roomInfoLabel, gameStatusLabel);
        List<Poker> pokers = new ArrayList<>();   //玩家手牌
        int[] indexs = new int[20];   //玩家手牌索引

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
            List<Poker> selectedCards = getSelectedCards();
        });

        passButton.setOnAction(e -> {

        });

        // 玩家1的卡牌区（左侧玩家）
        VBox player1Box = new VBox(10);
        player1Box.setAlignment(Pos.CENTER);
        Label player1Name = new Label("玩家1");
        VBox player1Cards = new VBox(5); // 每张手牌独占一行
        Label card1 = new Label("♠A");
        Label card2 = new Label("♣10");
        Label card3 = new Label("♥K");
        player1Cards.getChildren().addAll(card1, card2, card3);
        player1Box.getChildren().addAll(player1Name, player1Cards);

        // 玩家3的卡牌区（右侧玩家）
        VBox player3Box = new VBox(10);
        player3Box.setAlignment(Pos.CENTER);
        Label player3Name = new Label("玩家3");
        VBox player3Cards = new VBox(5); // 每张手牌独占一行
        Label card3_1 = new Label("♠K");
        Label card3_2 = new Label("♦2");
        Label card3_3 = new Label("♣Q");
        player3Cards.getChildren().addAll(card3_1, card3_2, card3_3);
        player3Box.getChildren().addAll(player3Name, player3Cards);

        // 玩家2的卡牌区（底部玩家，手牌横向展示）
        VBox player2Box = new VBox(10);
        player2Box.setAlignment(Pos.CENTER);
        Label player2Name = new Label("玩家2");
        HBox player2Cards = new HBox(5); // 每张手牌横向展示
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
    public  void setChannel(Channel channel) {
        this.channel = channel;
    }
    public static void main(String[] args) {
        launch(args);
    }

    private static List<Poker> getSelectedCards(int[] indexs, List<Poker> pokers) {
        List<Poker> selectedCards = new ArrayList<>();
        selectedCards = PokerUtil.getPoker(indexs, pokers);
        return selectedCards;
    }
}
