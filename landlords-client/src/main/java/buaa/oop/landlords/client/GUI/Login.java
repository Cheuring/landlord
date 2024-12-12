package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.entities.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Login extends Application {
    private static final Object loginLock = new Object();
    private static boolean isLoggedIn = false;
    private static Stage primaryStage;
    TextField usernameField = new TextField();
    PasswordField passwordField = new PasswordField();

    static String inputUsername = "";

    @Override
    public void start(Stage Stage) {
        isLoggedIn = false;
        primaryStage = Stage;
        usernameField.setPromptText("请输入用户名");
        usernameField.setStyle("-fx-font-size: 14px; -fx-prompt-text-fill: #888888;");
        usernameField.setPrefWidth(250);

        passwordField.setPromptText("请输入密码");
        passwordField.setStyle("-fx-font-size: 14px; -fx-prompt-text-fill: #888888;");
        passwordField.setPrefWidth(250);

        // 创建登录按钮
        Button submitButton = new Button("登录");
        submitButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 5px;");
        submitButton.setOnAction(e -> {
            inputUsername = usernameField.getText();
            if (!inputUsername.isEmpty() && isValidNickname(inputUsername)) {
                isLoggedIn = true;
                synchronized (loginLock) {
                    if (isLoggedIn) {
                        loginLock.notify();
                    }
                }
                System.out.println("用户输入的用户名：" + inputUsername);
            } else {
                usernameField.setStyle("-fx-border-color: red; -fx-font-size: 14px;");
            }
        });

        // 创建布局容器
        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                new Label("用户名："),
                usernameField,
                new Label("密码："),
                passwordField,
                submitButton
        );
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");


        // 设置场景
        Scene scene = new Scene(layout);
        scene.setFill(Color.WHITE);

        primaryStage.setScene(scene);
        primaryStage.setTitle("登录");
        primaryStage.setResizable(false);
        GUIUtil.cancelHandler(primaryStage);
        primaryStage.show();
    }

    public boolean isValidNickname(String nickname) {
        if (nickname == null) {
            return false;
        }
        if (nickname.length() > 16) {
            return false;
        }
        if (!nickname.matches("[a-zA-Z0-9_]+")) {
            return false;
        }
        return true;
    }

    public static String getNickname() {
        synchronized (loginLock) {
            while (!isLoggedIn) {
                try {
                    loginLock.wait(); // 阻塞等待
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("登录等待被中断", e);
                }
            }
        }
        return inputUsername;
    }
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
