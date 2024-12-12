package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.GUIUtil;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameSettlement extends Application {


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("游戏结算");

        // 创建根布局
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        // 创建标题
        Label titleLabel = new Label("游戏结算");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        root.getChildren().add(titleLabel);

        // 创建网格布局
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        // 添加标签栏
        Label nameLabel = new Label("姓名");
        Label scoreChangeLabel = new Label("分数变化");
        Label currentScoreLabel = new Label("当前分数");
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(scoreChangeLabel, 1, 0);
        gridPane.add(currentScoreLabel, 2, 0);

        // 添加用户信息栏
        String[] usernames = {"玩家1", "玩家2", "玩家3"};
        int[] scoreChanges = {10, -5, 15};
        int[] currentScores = {100, 95, 115};

        for (int i = 0; i < usernames.length; i++) {
            Label username = new Label(usernames[i]);
            Label scoreChange = new Label(String.valueOf(scoreChanges[i]));
            Label currentScore = new Label(String.valueOf(currentScores[i]));

            gridPane.add(username, 0, i + 1);
            gridPane.add(scoreChange, 1, i + 1);
            gridPane.add(currentScore, 2, i + 1);
        }

        // 添加确认按钮
        Button confirmButton = new Button("确认");
        confirmButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 5px;");
        confirmButton.setOnAction(e -> {
            // 确认按钮点击事件处理逻辑
            handleConfirmButton();
        });

        // 创建底部布局
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(confirmButton);

        // 将网格布局和底部布局添加到根布局
        root.getChildren().add(gridPane);
        root.getChildren().add(bottomBox);

        // 创建场景
        Scene scene = new Scene(root, 400, 300);
        scene.setFill(Color.WHITE);

        // 设置并显示主窗口
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        GUIUtil.cancelHandler(primaryStage);
        primaryStage.show();
    }

    private void handleConfirmButton() {
        // 待定的确认按钮点击事件处理方法
        System.out.println("确认按钮被点击");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
