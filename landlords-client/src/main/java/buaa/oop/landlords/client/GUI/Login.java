package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.utils.MapUtil;
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
    public static boolean isLoggedIn = false;
    private static Stage primaryStage;
    TextField usernameField = new TextField();
    PasswordField passwordField = new PasswordField();

    static String inputUsername = "";
    private static boolean flag = true;

    @Override
    public void start(Stage Stage) {
        primaryStage = Stage;
        usernameField.setPromptText("请输入用户名(4-16个字母与数字)");
        usernameField.setStyle("-fx-font-size: 14px; -fx-prompt-text-fill: #888888;");
        usernameField.setPrefWidth(250);

        passwordField.setPromptText("请输入密码");
        passwordField.setStyle("-fx-font-size: 14px; -fx-prompt-text-fill: #888888;");
        passwordField.setPrefWidth(250);

        Button submitButton = new Button("注册");
        submitButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 5px;");
        submitButton.setOnAction(e -> {
            if(flag){
                flag = false;
                inputUsername = usernameField.getText();
                if (!inputUsername.isEmpty() && isValidNickname(inputUsername)) {
                    isLoggedIn = true;
                    inputUsername = MapUtil.newInstance()
                            .put("operation",0)
                            .put("username",usernameField.getText())
                            .put("password",passwordField.getText())
                            .json();
                    User.getINSTANCE().setNickname(usernameField.getText());
                    synchronized (loginLock) {
                        if (isLoggedIn) {
                            loginLock.notify();
                        }
                    }
                    System.out.println("用户输入的用户名：" + inputUsername);
                } else {
                    flag=true;
                    usernameField.clear();
                    usernameField.setStyle("-fx-border-color: red; -fx-font-size: 14px;");
                }
            }
        });
        Button LoginButton = new Button("登录");
        LoginButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 5px;");
        LoginButton.setOnAction(e -> {
            if(flag) {
                flag = false;
                inputUsername = usernameField.getText();
                if (!inputUsername.isEmpty() && isValidNickname(inputUsername)) {
                    isLoggedIn = true;
                    inputUsername = MapUtil.newInstance()
                            .put("operation", 1)
                            .put("username", usernameField.getText())
                            .put("password", passwordField.getText())
                            .json();
                    User.getINSTANCE().setNickname(usernameField.getText());
                    synchronized (loginLock) {
                        if (isLoggedIn) {
                            loginLock.notify();
                        }
                    }
                    System.out.println("用户输入的用户名：" + inputUsername);
                } else {
                    usernameField.clear();
                    flag=true;
                    usernameField.setStyle("-fx-border-color: red; -fx-font-size: 14px;");
                }
            }
        });
        // 创建布局容器
        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                new Label("用户名："),
                usernameField,
                new Label("密码："),
                passwordField,
                submitButton,
                LoginButton
        );
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        Scene scene = new Scene(layout);
        scene.setFill(Color.WHITE);

        primaryStage.setScene(scene);
        primaryStage.setTitle("用户注册/登录");
        primaryStage.setResizable(false);
        GUIUtil.cancelHandler(primaryStage);
        primaryStage.show();
    }

    public boolean isValidNickname(String nickname) {
        if (nickname == null) {
            return false;
        }
        return nickname.matches("[a-zA-Z0-9_]{4,16}");
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

    public static void initFlag() {
        flag = true;
    }
}
