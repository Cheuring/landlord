package buaa.oop.landlords.client;


import buaa.oop.landlords.client.GUI.FailPopup;
import buaa.oop.landlords.client.GUI.Loading;
import buaa.oop.landlords.client.GUI.Login;
import buaa.oop.landlords.client.GUI.RoomHall;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GUIUtil {
    //显示错误提示信息
    // todo: fix the bug 后来调用会崩溃
    public static void renderScene(String msgSource,String msg ) {
        Platform.runLater(()->{
            FailPopup.showPopup(msgSource,msg);
        });
    }

    private static Alert closeAlert = null;

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
}
