package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.ChatRoom;
import buaa.oop.landlords.client.GUI.Login;
import buaa.oop.landlords.client.GUI.RoomHall;
import buaa.oop.landlords.client.GUIUtil;
import buaa.oop.landlords.client.SimpleClient;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import buaa.oop.landlords.common.enums.ServerEventCode;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 下一个状态为：ServerEventListener_CODE_CLIENT_NICKNAME_SET
 * 已完成
 */
@Slf4j
public class ClientEventListener_CODE_CLIENT_NICKNAME_SET extends ClientEventListener {
    public static final int NICKNAME_MAX_LENGTH = 16;

    @Override
    public void call(Channel channel, String data) {

        SimplePrinter.printNotice("Please set your nickname (upto " + NICKNAME_MAX_LENGTH + " characters)");
        SimplePrinter.printNotice("English letters, numbers, and underscores are legal");
        SimplePrinter.ServerLog("Please set your nickname:");
        try{
            Map<String,Object> datas= MapUtil.parse(data);
            if((int)datas.get("code")==1) GUIUtil.renderScene("登陆失败","密码错误");
            else if((int)datas.get("code")==2)GUIUtil.renderScene("登陆失败","用户不存在");
            else if((int)datas.get("code")==3)GUIUtil.renderScene(" 注册失败","用户名已存在");
            else if((int)datas.get("code")==4){
                GUIUtil.renderScene("服务器异常","请重新注册或登录");
            }
        }catch (Exception e){
        }
        Login.isLoggedIn=false;
        Login.initFlag();
        String name = Login.getNickname();

        log.info("nickname set to {}", name);
        pushToServer(channel, ServerEventCode.CODE_USER_LOGIN, name);
//            } else {
//                SimplePrinter.ServerLog("Invalid nickname!");
//                call(channel, data);
//            }

    }
}
//    public boolean isValidNickname(String nickname) {
//        if (nickname == null) {
//            return false;
//        }
//        if (nickname.length() > 16) {
//            return false;
//        }
//        if (!nickname.matches("[a-zA-Z0-9_]+")) {
//            return false;
//        }
//        return true;
//    }
//}
