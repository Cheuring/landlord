package buaa.oop.landlords.common.entities;

import buaa.oop.landlords.common.enums.ClientRole;
import buaa.oop.landlords.common.enums.ClientStatus;
import io.netty.channel.Channel;
import lombok.Data;

import java.util.List;

@Data
public class ClientEnd {
    private int id;

    private int score;

    private int scoreInc;

    private String nickName;

    private List<Poker> pokers;

    private ClientRole role = ClientRole.PEASANT;

    private ClientEnd pre, next;

    private Channel channel;

    private ClientStatus status;

    private int roomId;

    private int round;

    public ClientEnd(int id, Channel channel, ClientStatus status) {
        this.id = id;
        this.channel = channel;
        this.status = status;
    }

    public void resetRound() {
        round = 0;
    }

    public final void addRound() {round += 1;}

    public final String getNickName() { return nickName; }

    public final void addScore(int score) {
        this.score += score;
        this.scoreInc = score;
    }
}
