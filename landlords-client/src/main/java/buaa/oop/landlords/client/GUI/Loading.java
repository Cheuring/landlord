package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.SimpleClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import static buaa.oop.landlords.client.GUIUtil.renderScene;

@Slf4j
public class Loading extends Application {

    private static String host;
    private static int port;
    private static Stage primaryStage;

    public static void setConnectionDetails(String host, int port) {
        Loading.host = host;
        Loading.port = port;
    }

    @Override
    public void start(Stage primaryStage) {
        Loading.primaryStage = primaryStage;

        // 创建加载界面
        Label statusLabel = new Label("正在连接服务器...");
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        StackPane root = new StackPane();
        root.getChildren().addAll(progressBar, statusLabel);
        StackPane.setAlignment(statusLabel, javafx.geometry.Pos.TOP_CENTER);
        StackPane.setAlignment(progressBar, javafx.geometry.Pos.BOTTOM_CENTER);

        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("加载中...");
        GUIUtil.cancelHandler(primaryStage);
        primaryStage.show();

        // 启动后台线程连接服务器
        new Thread(() -> {
            try {
                for (double i = 0; i <= 1.0; i += 0.1) {
                    final double progress = i;
                    Thread.sleep(100); // 模拟连接延迟
                    Platform.runLater(() -> progressBar.setProgress(progress)); // 更新进度条
                }

                boolean connectionSuccess = SimpleClient.connect("localhost", 32112);

                Platform.runLater(() -> {
                    if (connectionSuccess) {
                        statusLabel.setText("连接成功！");
                        // 连接成功后跳转到登录界面
                        Login login = new Login();
                        Stage loginStage = new Stage();
                        login.start(loginStage);
                        GUIUtil.autoCloseAlertHandler(loginStage);
                        primaryStage.close();  // 关闭加载界面
                    } else {
                        statusLabel.setText("连接失败！");
                        renderScene("我去","连接失败");
                    }
                });
            } catch (Exception e) {
                log.error("连接失败", e);
                Platform.runLater(() -> statusLabel.setText("连接失败"));
                renderScene("我去","连接失败");
            }
        }).start();
    }



    public static void main(String[] args) {
        setConnectionDetails("localhost", 32112);
        launch(args);
    }
}
