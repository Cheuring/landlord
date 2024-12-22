package buaa.oop.landlords.client.GUI;

import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.entities.User;
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

import java.util.List;
import java.util.Map;

public class GameSettlement extends Application {
    private static List<Map<String, Object>> scores ;
    public static void setScores(List<Map<String, Object>> scores) {
        GameSettlement.scores = scores;
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("游戏结算");

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        Label titleLabel = new Label("游戏结算");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        root.getChildren().add(titleLabel);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        Label nameLabel = new Label("姓名");
        Label scoreChangeLabels = new Label("分数变化");
//        Label currentScoreLabels = new Label("当前分数");
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(scoreChangeLabels, 1, 0);
//        gridPane.add(currentScoreLabels, 2, 0);

        int row = 1;  // 起始行数
        for (Map<String, Object> score : scores) {
            String name = (String) score.get("nickName");
            int scoreChange = (int) score.get("scoreInc");
            int currentScore = (int) score.get("score");
            if(name.equals(User.getINSTANCE().getNickname())){
                User.getINSTANCE().setScore(User.getINSTANCE().getScore()+currentScore);
            }
            Label username = new Label(name);
            Label scoreChangeLabel = new Label(String.valueOf(scoreChange));
//            Label currentScoreLabel = new Label(String.valueOf(currentScore));

            gridPane.add(username, 0, row);
            gridPane.add(scoreChangeLabel, 1, row);
//            gridPane.add(currentScoreLabel, 2, row);
            row++;
        }

        Button confirmButton = new Button("返回大厅");
        confirmButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 5px;");
        confirmButton.setOnAction(e -> {
            Stage stage = GameRoom.getPrimaryStage();
            primaryStage.close();
            stage.close();
            RoomHall.roomHallDisplay();
        });

        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(confirmButton);

        root.getChildren().add(gridPane);
        root.getChildren().add(bottomBox);

        Scene scene = new Scene(root, 400, 300);
        scene.setFill(Color.WHITE);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        GUIUtil.cancelHandler(primaryStage);
        primaryStage.showAndWait();

    }
}
