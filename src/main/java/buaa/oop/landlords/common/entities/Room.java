package buaa.oop.landlords.common.entities;

import buaa.oop.landlords.common.enums.RoomStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
public class Room {
    private int id;

    private String roomOwner;

    private RoomStatus status;

    private LinkedList<ClientEnd> clientEndList;
    
    private int landlordId = -1;

    private List<Poker> landlordPokers;

    private PokerSell lastPokerShell;

    private int lastSellClient = -1;

    private int currentSellClient = -1;

    private int firstSellClient;

    private int scoreRate = 1;

    private int baseScore = 3;

    private final Object lock = new Object();

    public Room() {
    }

    public Room(int id) {
        this.id = id;
        this.clientEndList = new LinkedList<>();
        this.status = RoomStatus.WAIT;
    }

    public int getScore() {
        return this.baseScore * this.scoreRate;
    }

    public void initScoreRate() {
        this.scoreRate = 1;
    }

    public void increaseRate() {
        this.scoreRate *= 2;
    }

    public int addClient(ClientEnd client) {
        synchronized (lock) {
            if (clientEndList.size() >= 3) {
                return -1;
            }
            if (!clientEndList.isEmpty()) {
                client.setPre(clientEndList.getLast().getId());
                clientEndList.getLast().setNext(client.getId());
            }

            clientEndList.add(client);
            return clientEndList.size();
        }
    }

}
