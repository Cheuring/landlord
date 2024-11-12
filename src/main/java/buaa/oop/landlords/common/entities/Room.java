package buaa.oop.landlords.common.entities;

import buaa.oop.landlords.common.enums.RoomStatus;
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

    private Map<Integer, ClientEnd> clientEndMap;
    
    @Setter
    @Getter
    private LinkedList<ClientEnd> clientEndList;
    
    @Setter
    @Getter
    private int landlordId = -1;

    @Setter
    @Getter
    private List<Poker> landlordPokers;

    private PokerSell lastPokerShell;

    @Setter
    @Getter
    private int lastSellClient = -1;

    private int currentSellClient = -1;

    @Setter
    @Getter
    private int firstSellClient;

    @Setter
    @Getter
    private int scoreRate = 1;

    @Setter
    @Getter
    private int baseScore = 3;

    public Room() {
    }

    public Room(int id) {
        this.id = id;
        this.clientEndMap = new ConcurrentSkipListMap<>();
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

    public final PokerSell getLastPokerShell() {
        return lastPokerShell;
    }

    public final void setLastPokerShell(PokerSell lastPokerShell) {
        this.lastPokerShell = lastPokerShell;
    }

    public final int getCurrentSellClient() {
        return currentSellClient;
    }

    public final void setCurrentSellClient(int currentSellClient) {
        this.currentSellClient = currentSellClient;
    }

    public final String getRoomOwner() {
        return roomOwner;
    }

    public final void setRoomOwner(String roomOwner) {
        this.roomOwner = roomOwner;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final RoomStatus getStatus() {
        return status;
    }

    public final void setStatus(RoomStatus status) {
        this.status = status;
    }

    public final Map<Integer, ClientEnd> getClientEndMap() {
        return clientEndMap;
    }

    public final void setClientEndMap(Map<Integer, ClientEnd> clientEndMap) {
        this.clientEndMap = clientEndMap;
    }

}
