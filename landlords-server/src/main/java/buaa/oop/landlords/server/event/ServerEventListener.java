package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.enums.ServerEventCode;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class ServerEventListener {

    public abstract void call(ClientEnd event, String data);

    public final static Map<ServerEventCode, ServerEventListener> LISTENER_MAP = new HashMap<>();

    private final static String LISTENER_PREFIX = "buaa.oop.landlords.server.event.ServerEventListener_";

    // lazy load
    @SuppressWarnings("unchecked")
    public static ServerEventListener get(ServerEventCode code) {
        ServerEventListener listener = null;
        try {
            if (ServerEventListener.LISTENER_MAP.containsKey(code)) {
                listener = ServerEventListener.LISTENER_MAP.get(code);
            } else {
                String eventListener = LISTENER_PREFIX + code.name();
                Class<ServerEventListener> listenerClass = (Class<ServerEventListener>) Class.forName(eventListener);
                listener = listenerClass.getConstructor().newInstance();
                ServerEventListener.LISTENER_MAP.put(code, listener);
            }
            return listener;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
