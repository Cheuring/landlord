package buaa.oop.landlords.client.event;

import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class ClientEventListener {
    public abstract void call(Channel channel, String data);

    public final static Map<ClientEventCode, ClientEventListener> LISTENER_MAP = new HashMap<>();

    private final static String LISTENER_PREFIX = "buaa.oop.landlords.client.event.ClientEventListener_";

    // lazy load
    @SuppressWarnings("unchecked")
    public static ClientEventListener get(ClientEventCode code) {
        ClientEventListener listener = null;
        try {
            if (ClientEventListener.LISTENER_MAP.containsKey(code)) {
                listener = ClientEventListener.LISTENER_MAP.get(code);
            } else {
                String eventListener = LISTENER_PREFIX + code.name();
                Class<ClientEventListener> listenerClass = (Class<ClientEventListener>) Class.forName(eventListener);
                listener = listenerClass.getConstructor().newInstance();
                ClientEventListener.LISTENER_MAP.put(code, listener);
            }
            return listener;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected ChannelFuture pushToServer(Channel channel, ServerEventCode code, String data) {
        return ChannelUtil.pushToServer(channel, code, data);
    }

    protected ChannelFuture pushToServer(Channel channel, ServerEventCode code) {
        return pushToServer(channel, code, null);
    }
}
