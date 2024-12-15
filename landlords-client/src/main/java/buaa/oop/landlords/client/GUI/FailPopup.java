package buaa.oop.landlords.client.GUI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FailPopup {
    public static void showPopup(String title, String message) {
            // 创建弹窗
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // 设置模态窗口
            popupStage.initStyle(StageStyle.TRANSPARENT); // 无边框，透明背景
            popupStage.setTitle(title);

            // 弹窗布局
            BorderPane layout = new BorderPane();
            layout.setPadding(new Insets(10));
            layout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 10;");

            // 顶部右上角关闭按钮
            Button closeButton = new Button("❌");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 16; -fx-text-fill: darkred;");
            closeButton.setOnAction(e -> popupStage.close());

            StackPane closeButtonContainer = new StackPane(closeButton);
            closeButtonContainer.setAlignment(Pos.TOP_RIGHT);
            closeButtonContainer.setPadding(new Insets(5, 5, 0, 0));

            // 弹窗内容
            Text titleText = new Text(title);
            titleText.setFont(Font.font("Arial", 20));
            titleText.setFill(Color.DARKBLUE);
            titleText.setEffect(new DropShadow(2, Color.LIGHTGRAY));

            Text messageText = new Text(message);
            messageText.setFont(Font.font("Arial", 16));
            messageText.setFill(Color.DARKGRAY);

            VBox contentBox = new VBox(10, titleText, messageText);
            contentBox.setAlignment(Pos.CENTER);
            contentBox.setPadding(new Insets(20));

            // 底部按钮区域
            Button confirmButton = new Button("确定");
            confirmButton.setStyle(
                    "-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 5;");
            confirmButton.setOnAction(e -> popupStage.close());

            StackPane buttonContainer = new StackPane(confirmButton);
            buttonContainer.setAlignment(Pos.CENTER);
            buttonContainer.setPadding(new Insets(10));

            // 布局设置
            layout.setTop(closeButtonContainer);
            layout.setCenter(contentBox);
            layout.setBottom(buttonContainer);

            // 设置弹窗场景
            Scene scene = new Scene(layout, 350, 250);
            scene.setFill(Color.TRANSPARENT); // 设置透明背景
            popupStage.setScene(scene);

            // 显示弹窗
            popupStage.showAndWait();
    }
}
