package buaa.oop.landlords.common.utils;

import buaa.oop.landlords.common.entities.Msg;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ServerEventCode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class ChannelUtil {

    public static ChannelFuture pushToClient(Channel channel, ClientEventCode code, String data) {
        return pushToClient(channel, code, data, null);
    }

    public static ChannelFuture pushToClient(Channel channel, ClientEventCode code, String data, String info) {
        if(channel != null) {
            Msg msg = new Msg();
            msg.setCode(code.toString());
            msg.setData(data);
            msg.setInfo(info);
            return channel.writeAndFlush(msg);
        }
        return null;
    }

    public static ChannelFuture pushToServer(Channel channel, ServerEventCode code, String data) {
        Msg msg = new Msg();
        msg.setCode(code.toString());
        msg.setData(data);
        return channel.writeAndFlush(msg);
    }
}
