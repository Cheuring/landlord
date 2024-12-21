package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.ClientContainer;
import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.client.event.ClientEventListener_CODE_SHOW_OPTIONS;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.entities.PokerSell;
import buaa.oop.landlords.common.enums.*;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.common.utils.PokerUtil;
import com.fasterxml.jackson.core.type.TypeReference;
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
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static buaa.oop.landlords.common.utils.ChannelUtil.pushToClient;
import static buaa.oop.landlords.common.utils.ChannelUtil.pushToServer;

public class GameRoom extends Application {
    private  Channel channel;
    private int roomId;
    private static Label gameStatusLabel = new Label();
    private static HBox landlordCards = new HBox(-50);
    private static HBox actionButtonsBox = new HBox(20);
    private static VBox player1Cards = new VBox(-50);
    private static VBox player3Cards = new VBox(-50);
    private static HBox player2Cards = new HBox(-50);
    //indexes数组用于记录第i张牌是否被按下
    private static int[] indexes = new int[20];

    private static VBox player1LastPokers = new VBox(-50);
    private static VBox p3LastPokers = new VBox(-50);

    private static Label player1Name = new Label();
    private static Label player2Name = new Label();
    private static Label player3Name = new Label();

    private static Label player1Role = new Label();
    private static Label player2Role = new Label();
    private static Label player3Role = new Label();

    private static Label player1Score = new Label();
    private static Label player2Score = new Label();
    private static Label player3Score = new Label();

    @Getter
    private static Stage primaryStage;

    private static String data;
    private static List<Poker> pokers;
    private static List<Poker> lastPokers;
    private static String lastSellClientNickname;
    private static Integer lastSellClientId;

    private static final Object gameRoomLock = new Object();
    public static boolean isElect=false;
    public static String robScore="";
    @Override
    public void start(Stage primaryStage) {
        GameRoom.primaryStage = primaryStage;
        primaryStage.setTitle("三人斗地主房间");

        // 房间信息展示区
        VBox roomInfoBox = new VBox(10);
        roomInfoBox.setAlignment(Pos.TOP_CENTER);
        Label roomInfoLabel = new Label("房间号: "+Integer.toString(roomId));
        gameStatusLabel.setText("游戏状态: 等待其他玩家加入");
        landlordCards.setAlignment(Pos.CENTER);
        roomInfoBox.getChildren().addAll(roomInfoLabel, gameStatusLabel, landlordCards);

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
        topLayout.setAlignment(Pos.TOP_CENTER);
        topLayout.setPadding(new Insets(10));
        topLayout.getChildren().addAll(roomInfoBox, exitButton);
        HBox.setHgrow(roomInfoBox, Priority.ALWAYS);
        topLayout.setSpacing(10);

        // 操作按钮区，水平排列
        actionButtonsBox.setAlignment(Pos.CENTER);

        // 玩家1的卡牌区（左侧玩家）
        VBox player1Box = new VBox(10);
        player1Box.setAlignment(Pos.CENTER);
        player1Role.setText("");
        player1Box.getChildren().addAll(player1Role, player1Name, player1Score, player1Cards);

        VBox p1LastPokers = new VBox(10);

        // 玩家3的卡牌区（右侧玩家）
        VBox player3Box = new VBox(10);
        player3Box.setAlignment(Pos.CENTER);
        player3Role.setText("");
        player3Name.setText("玩家3");
        player3Box.getChildren().addAll(player3Role, player3Name, player3Score, player3Cards);

        // 玩家2的卡牌区（底部玩家，手牌横向展示）
        VBox player2Box = new VBox(10);
        player2Box.setAlignment(Pos.CENTER);
        player2Role.setText("");
        player2Cards.setAlignment(Pos.CENTER);
        player2Box.getChildren().addAll(player2Role, player2Name, player2Score, player2Cards);

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
        landlordCards.getChildren().clear();
        actionButtonsBox.getChildren().clear();
        player1Cards.getChildren().clear();
        player2Cards.getChildren().clear();
        player3Cards.getChildren().clear();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setRoomStatus(String title) {
        gameStatusLabel.setText(title);
    }

    public static void setLandlordCards(List<Poker> pokers) {
        int cnt = pokers.size();
        for(int i = 0; i < cnt; i++) {
            Poker poker = pokers.get(i);
            int idx = poker.getLevel().getIdx();
            int value = poker.getType().getValue();
            ImageView imageView = GUIUtil.getPokerImage(idx, value);
            landlordCards.getChildren().add(imageView);
        }
    }

    public static void setPoint(String point, int i) {
        switch (i) {
            case 1:
                player1Score.setText("Score: " + point);
                break;
            case 2:
                player2Score.setText("Score: " + point);
                break;
            case 3:
                player3Score.setText("Score: " + point);
                break;
            default:
        }
    }

    public static void setPlayerName(String name,int i) {
        switch (i) {
            case 1:
                player1Name.setText(name);
                break;
            case 2:
                player2Name.setText(name);
                break;
            case 3:
                player3Name.setText(name);
                break;
            default:
        }
    }

    public static String getPlayerName(int i) {
        switch (i) {
            case 1:
                return player1Name.getText();
            case 2:
                return player2Name.getText();
            default:
                return player3Name.getText();
        }
    }

    public static void setPlayerRole(String role,int i) {
        switch (i) {
            case 1:
                player1Role.setText(role);
                break;
            case 2:
                player2Role.setText(role);
                break;
            case 3:
                player3Role.setText(role);
                break;
        }
    }

    public static void setData(String content) {
        data = content;
        Map<String, Object> roominfo = MapUtil.parse(data);
        pokers = JsonUtil.fromJson((String) roominfo.get("pokers"), new TypeReference<List<Poker>>() {});
        lastPokers = JsonUtil.fromJson((String) roominfo.get("lastSellPokers"), new TypeReference<List<Poker>>() {});
        lastSellClientNickname = (String) roominfo.get("lastSellClientName");
        lastSellClientId = (Integer) roominfo.get("lastSellClientId");
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
         int[] ans = new int[pos];
         for (int i = 0; i < pos; i++) {
             ans[i] = list[i];
             System.out.println(ans[i]);
         }
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
                System.out.println("1");
                if(indexes[finalI] == 0) {
                    indexes[finalI] = 1;
                    cardButton.setTranslateY(-10);
                }
                else {
                    indexes[finalI] = 0;
                    cardButton.setTranslateY(0);
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
            actionButtonsBox.getChildren().clear();
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

    public static void playButtonOn() {
        actionButtonsBox.getChildren().clear();
        Button playButton = new Button("出牌");
        playButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 10;");
        Button passButton = new Button("过牌");
        passButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 10;");
        if (lastSellClientId == null || lastSellClientId == User.INSTANCE.getId())
            actionButtonsBox.getChildren().addAll(playButton);
        else
            actionButtonsBox.getChildren().addAll(playButton, passButton);

        playButton.setOnAction(e -> {
            List<Poker> selectedCards = getSelectedCards(indexes, pokers);
            if(!selectedCards.isEmpty()) {
                PokerSell currentPokerSell = PokerUtil.checkPokerSell(selectedCards);
                PokerSell lastPokerSell = PokerUtil.checkPokerSell(lastPokers);
                if (currentPokerSell.getSellType() == SellType.ILLEGAL) {
                    SimplePrinter.printNotice("The combination is illegal!");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText(null);
                    alert.setContentText("牌型不合法");
                    alert.showAndWait();
                }

                else if (lastPokerSell.getSellType() != SellType.ILLEGAL && lastSellClientId != User.INSTANCE.getId()) {

                    if ((lastPokerSell.getSellType() != currentPokerSell.getSellType() || lastPokerSell.getPokers().size() != currentPokerSell.getPokers().size()) && currentPokerSell.getSellType() != SellType.BOMB && currentPokerSell.getSellType() != SellType.KING_BOMB) {
                        SimplePrinter.printNotice(String.format("Your combination is %s (%d), but the previous combination is %s (%d). Mismatch!", currentPokerSell.getSellType(), currentPokerSell.getPokers().size(), lastPokerSell.getSellType(), lastPokerSell.getPokers().size()));
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("警告");
                        alert.setHeaderText(null);
                        alert.setContentText("牌型不匹配");
                        alert.showAndWait();
                    }

                    else if (lastPokerSell.getScore() >= currentPokerSell.getScore()) {
                        SimplePrinter.printNotice("Your combination has lower rank than the previous. You cannot play this combination!");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("警告");
                        alert.setHeaderText(null);
                        alert.setContentText("大小不匹配");
                        alert.showAndWait();
                    }else{
                        String result = MapUtil.newInstance()
                                .put("poker", selectedCards)
                                .put("pokerSell", currentPokerSell)
                                .json();
                        actionButtonsBox.getChildren().clear();
                        pushToServer(ClientContainer.channel, ServerEventCode.CODE_GAME_POKER_PLAY, result);
                    }
                }
                else {
                    String result = MapUtil.newInstance()
                            .put("poker", selectedCards)
                            .put("pokerSell", currentPokerSell)
                            .json();
                    actionButtonsBox.getChildren().clear();
                    pushToServer(ClientContainer.channel, ServerEventCode.CODE_GAME_POKER_PLAY, result);
                }
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
            actionButtonsBox.getChildren().clear();
            pushToServer(ClientContainer.channel, ServerEventCode.CODE_GAME_POKER_PLAY_PASS, data);
        });
    }
}
