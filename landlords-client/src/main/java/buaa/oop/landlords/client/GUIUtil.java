package buaa.oop.landlords.client;


import buaa.oop.landlords.client.GUI.FailPopup;
import buaa.oop.landlords.client.GUI.Loading;
import buaa.oop.landlords.client.GUI.Login;
import buaa.oop.landlords.client.GUI.RoomHall;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class GUIUtil {
    //显示错误提示信息
    public static void renderScene(String msgSource,String msg ) {
        Platform.runLater(()->{
            FailPopup.showPopup(msgSource,msg);
        });
    }

}
