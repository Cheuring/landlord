package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.ClientContainer;
import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.client.enums.Assets;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private static VBox player1Cards = new VBox(-110);
    private static VBox player3Cards = new VBox(-110);

    private static HBox player2Cards = new HBox(-50);

    private static Label player1CardsCount;
    private static int player1CardsCnt = 0;
    private static Label player3CardsCount;
    private static int player3CardsCnt = 0;

    private static int[] indexes = new int[20];

    private static HBox player1LastPokers = new HBox(-50);
    private static HBox player2LastPokers = new HBox(-50);
    private static HBox player3LastPokers = new HBox(-50);

    private static Label player1Name = new Label();
    private static Label player2Name = new Label();
    private static Label player3Name = new Label();

    private static ImageView player1Role;
    private static ImageView player2Role;
    private static ImageView player3Role;

    private static Label player1Score;
    private static Label player2Score;
    private static Label player3Score;

    public static HBox getPlayer1LastPokers() {
        return player1LastPokers;
    }

    public static HBox getPlayer2LastPokers() {
        return player2LastPokers;
    }

    public static HBox getPlayer3LastPokers() {
        return player3LastPokers;
    }

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
        roomInfoLabel.setStyle("-fx-font-weight:bold; -fx-font-size: 20;");
        gameStatusLabel.setText("游戏状态: 等待其他玩家加入");
        gameStatusLabel.setStyle("-fx-font-weight:bold; -fx-font-size: 24;");
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
        player1Name.setStyle("-fx-font-weight:bold; -fx-font-size: 20;");
        player1Score.setStyle("fx-font-weight:bold; -fx-text-fill: WHITE; -fx-font-size: 20;");
        player1CardsCount.setStyle("-fx-font-weight:bold; -fx-text-fill: CYAN; -fx-font-size: 20;");
        player1Box.getChildren().addAll(player1Role, player1Name, player1Score, player1Cards, player1CardsCount);

        HBox player1=new HBox(10);
        player1LastPokers.setAlignment(Pos.CENTER);
        player1.getChildren().addAll(player1Box, player1LastPokers);

        // 玩家3的卡牌区（右侧玩家）
        VBox player3Box = new VBox(10);
        player3Box.setAlignment(Pos.CENTER);
        player3Name.setText("玩家3");
        player3Name.setStyle("-fx-font-weight:bold; -fx-font-size: 20;");
        player3Score.setStyle("fx-font-weight:bold; -fx-text-fill: WHITE; -fx-font-size: 20;");
        player3CardsCount.setStyle("-fx-font-weight:bold; -fx-text-fill: CYAN; -fx-font-size: 20;");
        player3Box.getChildren().addAll(player3Role, player3Name, player3Score, player3Cards, player3CardsCount);
        HBox player3=new HBox(10);
        player3LastPokers.setAlignment(Pos.CENTER);
        player3.getChildren().addAll(player3LastPokers, player3Box);

        // 玩家2的卡牌区（底部玩家，手牌横向展示）
        VBox player2Box = new VBox(10);
        player2Box.setAlignment(Pos.CENTER);
        player2Cards.setAlignment(Pos.CENTER);
        player2LastPokers.setAlignment(Pos.CENTER);
        player2Name.setStyle("-fx-font-weight:bold; -fx-font-size: 20;");
        player2Score.setStyle("fx-font-weight:bold; -fx-text-fill: WHITE; -fx-font-size: 20;");
        player2Box.getChildren().addAll(player2Role, player2Name, player2Score);
        HBox player22 =new HBox(10);
        player22.setAlignment(Pos.CENTER);
        player22.getChildren().addAll(player2Box, player2Cards);
        VBox player2=new VBox(10);
        player2.setAlignment(Pos.CENTER);
        player2.getChildren().addAll(player2LastPokers,player22);

        // 左右玩家布局
        BorderPane sidePlayersLayout = new BorderPane();
        sidePlayersLayout.setLeft(player1);
        sidePlayersLayout.setRight(player3);

        // 主布局（回字结构）
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topLayout);
        mainLayout.setCenter(actionButtonsBox);
        mainLayout.setBottom(player2);
        mainLayout.setLeft(player1);
        mainLayout.setRight(player3);

        Image backgroundImage = new Image("/images/background1.jpg");
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(1200);
        backgroundImageView.setFitHeight(700);
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.setOpacity(0.5);
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundImageView, mainLayout);

        Scene scene = new Scene(root, 1200, 700);

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> backgroundImageView.setFitWidth((double) newVal));
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> backgroundImageView.setFitHeight((double) newVal));

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
        player1Role = new ImageView();
        player2Role = new ImageView();
        player3Role = new ImageView();
        player1CardsCount = new Label();
        player3CardsCount = new Label();
        player1Score = new Label();
        player2Score = new Label();
        player3Score = new Label();
        clearLastPokers();
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

    public static HBox getPlayerLastPokers(int i) {
        switch (i) {
            case 1:
                return player1LastPokers;
            case 2:
                return player2LastPokers;
            default:
                return player3LastPokers;
        }
    }

    public static void clearLastPokers() {
        player1LastPokers.getChildren().clear();
        player2LastPokers.getChildren().clear();
        player3LastPokers.getChildren().clear();
    }

    public static void setPlayerRole(String role,int i) {
        switch (i) {
            case 1:
                if(role.equals("landlord"))
                    player1Role.setImage(GUIUtil.getAssetImage(Assets.LANDLORDS).getImage());
                else
                    player1Role.setImage(GUIUtil.getAssetImage(Assets.PEASANT).getImage());
                break;
            case 2:
                if(role.equals("landlord"))
                    player2Role.setImage(GUIUtil.getAssetImage(Assets.LANDLORDS).getImage());
                else
                    player2Role.setImage(GUIUtil.getAssetImage(Assets.PEASANT).getImage());
                break;
            case 3:
                if(role.equals("landlord"))
                    player3Role.setImage(GUIUtil.getAssetImage(Assets.LANDLORDS).getImage());
                else
                    player3Role.setImage(GUIUtil.getAssetImage(Assets.PEASANT).getImage());
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

    private static List<Poker> getSelectedCards(int[] index, List<Poker> pokers, int on) {
        List<Poker> selectedCards = new ArrayList<>();
        int[] list = new int[20];
        int pos = 0;
        for(int i = 0; i < 20; i++) {
             if(index[i] == on && i < pokers.size())
                 list[pos++] = i;
         }
         int[] ans = new int[pos];
         for (int i = 0; i < pos; i++) {
             ans[i] = list[i] + 1;
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
        if(player == 1) {
            ImageView imageView = GUIUtil.getPokerBackImage();
            player1CardsCount.setText("剩余手牌：" + String.valueOf(size));
            player1CardsCnt = size;
            player1Cards.getChildren().add(imageView);
        }
        else {
            ImageView imageView = GUIUtil.getPokerBackImage();
            player3CardsCount.setText("剩余手牌：" + String.valueOf(size));
            player3CardsCnt = size;
            player3Cards.getChildren().add(imageView);
        }
    }

    public static int getSurplus(int player) {
        if (player == 1)
            return player1CardsCnt;
        else
            return player3CardsCnt;
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
             updatePlayerArea(null,GUIUtil.getAssetImage(Assets.SCORE_ZERO),player2LastPokers);
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
        Button playButton = new Button();
        playButton.setGraphic(GUIUtil.getAssetImage(Assets.BTN_PLAY_CARD));
        playButton.setStyle("-fx-background-color: transparent; -fx-text-fill: transparent; -fx-font-size: 16; -fx-background-radius: 10;");
        Button passButton = new Button();
        passButton.setGraphic(GUIUtil.getAssetImage(Assets.BTN_PASS));
        passButton.setStyle("-fx-background-color: transparent; -fx-text-fill: transparent; -fx-font-size: 16; -fx-background-radius: 10;");
        if (lastSellClientId == null || lastSellClientId == User.INSTANCE.getId())
            actionButtonsBox.getChildren().addAll(playButton);
        else
            actionButtonsBox.getChildren().addAll(playButton, passButton);

        playButton.setOnAction(e -> {
            List<Poker> selectedCards = getSelectedCards(indexes, pokers, 1);
            List<Poker> remainingCards = getSelectedCards(indexes, pokers, 0);
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
                        displayPokers(remainingCards);
                        updatePlayerArea(selectedCards,null,player2LastPokers);
                        pushToServer(ClientContainer.channel, ServerEventCode.CODE_GAME_POKER_PLAY, result);
                    }
                }
                else {
                    String result = MapUtil.newInstance()
                            .put("poker", selectedCards)
                            .put("pokerSell", currentPokerSell)
                            .json();
                    actionButtonsBox.getChildren().clear();
                    displayPokers(remainingCards);
                    updatePlayerArea(selectedCards,null,player2LastPokers);
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
            updatePlayerArea(null,GUIUtil.getAssetImage(Assets.SHOW_PASS), player2LastPokers);
            pushToServer(ClientContainer.channel, ServerEventCode.CODE_GAME_POKER_PLAY_PASS, data);
        });
    }

    public static void updatePlayerArea(List<Poker> playerPokers, ImageView imageView,HBox hBox) {
        if(playerPokers == null || playerPokers.isEmpty()) {
            hBox.getChildren().clear();
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().add(imageView);
        }else{
            hBox.getChildren().clear();
            for(int i = 0; i < 20; i++)
                indexes[i] = 0;
            int cnt = playerPokers.size();
            for(int i = 0; i < cnt; i++) {
                Poker poker = playerPokers.get(i);
                int idx = poker.getLevel().getIdx();
                int value = poker.getType().getValue();
                ImageView imageview = GUIUtil.getPokerImage(idx, value);
                hBox.getChildren().add(imageview);
            }
        }
    }
}
