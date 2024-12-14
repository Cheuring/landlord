package buaa.oop.landlords.client.GUI;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import javafx.util.Duration;

public class HintPopup {
    public static void showPopup(String title, String message, int durationInSeconds) {
        // 创建弹窗
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.setTitle(title);
        popupStage.setAlwaysOnTop(true);

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 10;");

        Button closeButton = new Button("❌");
        closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 16; -fx-text-fill: darkred;");
        closeButton.setOnAction(e -> popupStage.close());

        StackPane closeButtonContainer = new StackPane(closeButton);
        closeButtonContainer.setAlignment(Pos.TOP_RIGHT);
        closeButtonContainer.setPadding(new Insets(5, 5, 0, 0));

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

        PauseTransition pause = new PauseTransition(Duration.seconds(durationInSeconds));
        pause.setOnFinished(e -> popupStage.close());
        pause.play();

        Scene scene = new Scene(layout, 350, 250);
        scene.setFill(Color.TRANSPARENT);
        popupStage.setScene(scene);

        layout.setTop(closeButtonContainer);
        layout.setCenter(contentBox);

        popupStage.show();

        popupStage.setOnHidden(e -> pause.stop());
    }
}
