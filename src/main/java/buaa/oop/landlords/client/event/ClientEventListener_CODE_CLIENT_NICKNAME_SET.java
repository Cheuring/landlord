package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.print.SimperWriter;
import buaa.oop.landlords.common.print.SimplePrinter;
import io.netty.channel.Channel;
import buaa.oop.landlords.common.enums.ServerEventCode;

public class ClientEventListener_CODE_CLIENT_NICKNAME_SET extends ClientEventListener {

    @Override
    public void call(Channel channel, String data) {
        SimplePrinter.ServerLog("Please set your nickname:");
        User user = User.INSTANCE;
        String name = SimperWriter.write(user.getNickname(), "nickname");

        // todo: check name validity

        user.setNickname(name);
        pushToServer(channel, ServerEventCode.CODE_CLIENT_NICKNAME_SET, name);
    }
}
