package buaa.oop.landlords.client;


import buaa.oop.landlords.client.GUI.FailPopup;
import buaa.oop.landlords.client.GUI.HintPopup;
import buaa.oop.landlords.client.enums.Assets;
import buaa.oop.landlords.common.entities.Poker;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static buaa.oop.landlords.client.ClientContainer.gameRoom;

public class GUIUtil {
    //显示错误提示信息
    public static void renderScene(String msgSource,String msg ) {
        Platform.runLater(()->{
            FailPopup.showPopup(msgSource,msg);
        });
    }
    public static void renderScene(String msgSource,String msg, int time) {
        Platform.runLater(()->{
            HintPopup.showPopup(msgSource,msg,time);
        });
    }
    public static void renderScene(List<Poker> playerPokers, ImageView imageView, HBox hBox) {
        Platform.runLater(()->{
            gameRoom.updatePlayerArea(playerPokers,imageView,hBox);
        });
    }


    private static Alert closeAlert = null;
    private static Image pokerImages[][] = new Image[4][13];
    private static Image pokerImagesRest[] = new Image[3];
    private static ArrayList<Image> assets = new ArrayList<>();

    static {
        Image image = new Image(GUIUtil.class.getResource("/images/pokers.png").toExternalForm());
        double tileWidth = image.getWidth() / 13;
        double tileHeight = image.getHeight() / 5;

        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 13; ++j){
                pokerImages[i][j] = new WritableImage(image.getPixelReader(), (int)(j * tileWidth), (int)(i * tileHeight), (int)tileWidth, (int)tileHeight);
            }
        }

        // 0: 小王, 1: 大王, 2: 背面
        pokerImagesRest[0] = new Image(GUIUtil.class.getResource("/images/joker_0.png").toExternalForm());
        pokerImagesRest[1] = new Image(GUIUtil.class.getResource("/images/joker_1.png").toExternalForm());
        pokerImagesRest[2] = new WritableImage(image.getPixelReader(), (int)(2 * tileWidth), (int)(4 * tileHeight), (int)tileWidth, (int)tileHeight);

        // Load other assets
        for (int i = 1; i <= 12; ++i) {
            assets.add(new Image(GUIUtil.class.getResource("/images/" + i + ".png").toExternalForm()));
        }
    }

    public static void cancelHandler(Stage primaryStage) {
        primaryStage.setOnCloseRequest(event -> {
            if (closeAlert == null) { // 检查确认关闭弹窗是否已经显示
                closeAlert = new Alert(Alert.AlertType.CONFIRMATION);
                closeAlert.setTitle("确认关闭");
                closeAlert.setHeaderText("您确定要关闭应用程序吗？");
                closeAlert.setContentText("请选择您的选项：");

                ButtonType yesButton = new ButtonType("是");
                ButtonType noButton = new ButtonType("否", ButtonBar.ButtonData.CANCEL_CLOSE);

                closeAlert.getButtonTypes().setAll(yesButton, noButton);

                closeAlert.setOnCloseRequest(closeEvent -> {
                    closeAlert = null; // 关闭弹窗时重置引用
                });

                Optional<ButtonType> result = closeAlert.showAndWait();
                if (result.isPresent() && result.get() == yesButton) {
                    System.out.println("关闭应用程序");
                    System.exit(0); // 退出应用程序
                } else {
                    event.consume(); // 消费事件，阻止关闭窗口
                }
            }
        });
    }

    public static void autoCloseAlertHandler(Stage primaryStage) {
        if (closeAlert != null) { // 如果确认关闭弹窗已经显示，取消显示
            closeAlert.close();
        }
    }

    public static ImageView getPokerImage(int idx, int type) {
        ImageView res;
        if(type != -1){
            res = new ImageView(pokerImages[type][idx]);
        }
        else {
            res = new ImageView(pokerImagesRest[idx]);
        }
        res.setFitWidth(80);
        res.setFitHeight(120);
        return res;
    }

    public static ImageView getPokerBackImage() {
        ImageView imageView = new ImageView(pokerImagesRest[2]);
        imageView.setFitWidth(80);
        imageView.setFitHeight(120);
        return imageView;
    }

    public static ImageView getAssetImage(Assets asset){
        return new ImageView(assets.get(asset.getIdx()));
    }
}
