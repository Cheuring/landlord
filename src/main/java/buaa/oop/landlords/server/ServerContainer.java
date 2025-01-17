package buaa.oop.landlords.server;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Room;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerContainer {

    public static int port = 8080;

    public final static Map<Integer, Room> ROOM_MAP = new ConcurrentHashMap<>();
    public final static Map<Integer, ClientEnd> CLIENT_END_MAP = new ConcurrentHashMap<>();
    public final static Map<String, Integer> CHANNEL_ID_MAP = new ConcurrentHashMap<>();
    public final static Map<String, Integer> CLIENT_NAME_TO_ID = new ConcurrentHashMap<>();
    private final static AtomicInteger CLIENT_ATOMIC_ID = new AtomicInteger(1);
    private final static AtomicInteger ROOM_ATOMIC_ID = new AtomicInteger(1);

    public static int getNewClientId() {
        return CLIENT_ATOMIC_ID.getAndIncrement();
    }

    public static int getNewRoomId() {
        return ROOM_ATOMIC_ID.getAndIncrement();
    }

    public static Map<Integer, Room> getRoomMap() {
        return ROOM_MAP;
    }

    public static void removeRoom(int id) {
        ROOM_MAP.remove(id);
    }

    public static void addRoom(Room room) {
        ROOM_MAP.put(room.getId(), room);
    }

    public static Room getRoom(int id) {
        return ROOM_MAP.get(id);
    }

    public static boolean containsRoom(int id) {
        return ROOM_MAP.containsKey(id);
    }

    public static boolean containsClient(int id) {
        return CLIENT_END_MAP.containsKey(id);
    }
    public static boolean containsClient(String name) {
        return CLIENT_NAME_TO_ID.containsKey(name);
    }

    public static ClientEnd getClient(int id) {
        return CLIENT_END_MAP.get(id);
    }
    public static ClientEnd getClient(String name) {
        return CLIENT_END_MAP.get(CLIENT_NAME_TO_ID.get(name));
    }

    public static void addClient(ClientEnd client) {
        CLIENT_END_MAP.put(client.getId(), client);
        CLIENT_NAME_TO_ID.put(client.getNickname(), client.getId());
    }
}
