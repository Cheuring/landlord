package buaa.oop.landlords.client.event;

import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;
import buaa.oop.landlords.common.enums.ServerEventCode;
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
        Map<String, Object> map = MapUtil.parse(data);
        int code = (int) map.getOrDefault("code", -1);
        switch (code){
            case 1:
                SimplePrinter.printWarning("logging failed: password error");
                break;
            case 2:
                SimplePrinter.printWarning("logging failed: username not exist");
                break;
            case 3:
                SimplePrinter.printWarning("register failed: username already exist");
                break;
            case 4:
                SimplePrinter.printWarning("server error: please try later");
                break;
        }

//        SimplePrinter.ServerLog("Please set your nickname:\n\tformat: [a-zA-Z0-9_]{4,16}");
//        User user = User.INSTANCE;
        String method = SimpleWriter.write("Player", "logging/register");
        String username = SimpleWriter.write("Player", "username");
        String password = SimpleWriter.write("Player", "password");

        if(!isValidNickname(username)){
            SimplePrinter.printNotice("username not valid: [a-zA-Z0-9_]{4,16}");
            call(channel, null);
            return;
        }
        boolean isLogging = method.equalsIgnoreCase("logging") || method.equalsIgnoreCase("l");

        if(!isLogging &&
            !method.equalsIgnoreCase("register") && !method.equalsIgnoreCase("r")){
            SimplePrinter.printNotice("Invalid method: %s".formatted(method));
            call(channel, null);
            return;
        }

        pushToServer(channel,
                ServerEventCode.CODE_USER_LOGIN,
                MapUtil.newInstance()
                        .put("operation", isLogging ? 1 : 0)
                        .put("username", username)
                        .put("password", password)
                        .json()
                );
    }

    public boolean isValidNickname(String nickname) {
        if (nickname == null) {
            return false;
        }
        return nickname.matches("[a-zA-Z0-9_]{4,16}");
    }
}
