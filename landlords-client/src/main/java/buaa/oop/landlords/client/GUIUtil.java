package buaa.oop.landlords.client;


import buaa.oop.landlords.client.GUI.FailPopup;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

public class GUIUtil {
    //显示错误提示信息
    public static void renderScene(String msgSource,String msg ) {
        Platform.runLater(()->{
            FailPopup.showPopup(msgSource,msg);
        });
    }

    private static Alert closeAlert = null;
    private static ImageView pokerImages[][] = new ImageView[4][13];
    private static ImageView pokerImagesRest[] = new ImageView[3];


    static {
        // todo: 加载大小王 背面
        Image image = new Image(GUIUtil.class.getResource("/images/pokers.png").toExternalForm());
        double tileWidth = image.getWidth() / 13;
        double tileHeight = image.getHeight() / 5;

        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 13; ++j){
                pokerImages[i][j] = new ImageView(image);
                pokerImages[i][j].setViewport(new javafx.geometry.Rectangle2D(j * tileWidth, i * tileHeight, tileWidth, tileHeight));
                pokerImages[i][j].setFitWidth(80);
                pokerImages[i][j].setFitHeight(120);
            }
        }

        for(int i = 0; i < 3; ++i){
            pokerImagesRest[i] = new ImageView(image);
            pokerImagesRest[i].setViewport(new javafx.geometry.Rectangle2D(i * tileWidth, 4 * tileHeight, tileWidth, tileHeight));
            pokerImagesRest[i].setFitWidth(80);
            pokerImagesRest[i].setFitHeight(120);
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
        if(type != -1){
            return pokerImages[type][idx];
        }

        return pokerImagesRest[idx];
    }

    public static ImageView getPokerBackImage() {
        return pokerImagesRest[2];
    }
}
