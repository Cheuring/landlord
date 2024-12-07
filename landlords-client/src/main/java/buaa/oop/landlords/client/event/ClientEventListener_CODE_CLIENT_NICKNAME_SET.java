package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.ChatRoom;
import buaa.oop.landlords.client.SimpleClient;
import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.print.SimplePrinter;
import io.netty.channel.Channel;
import buaa.oop.landlords.common.enums.ServerEventCode;
import lombok.extern.slf4j.Slf4j;

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
        User user = User.INSTANCE;
        String name = SimpleWriter.write(user.getNickname(), "nickname");

        if (isValidNickname(name)) {
            user.setNickname(name);
            SimpleClient.chatRoom = ChatRoom.getInstance(channel);
            log.info("nickname set to {}", name);
            pushToServer(channel, ServerEventCode.CODE_CLIENT_NICKNAME_SET, name);
        } else {
            SimplePrinter.ServerLog("Invalid nickname!");
            call(channel, data);
        }

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
}
