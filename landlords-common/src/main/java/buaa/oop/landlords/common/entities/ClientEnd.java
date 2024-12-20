package buaa.oop.landlords.common.entities;

import buaa.oop.landlords.common.enums.ClientRole;
import buaa.oop.landlords.common.enums.ClientStatus;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ClientEnd {
    @Data
    @NoArgsConstructor
    public class User {
        private String name;
        private int score;
    }

    private int id;

    private int score;

    private User user = new User();

    private int scoreInc;

    private List<Poker> pokers;

    private ClientRole role = ClientRole.PEASANT;

    private int pre, next;

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

    public final String getNickname() { return user.name; }

    public final void setNickname(String nickname) { user.name = nickname; }

    public final void addScore(int score) {
        this.score += score;
        this.scoreInc = score;
    }

    public int getTotalScore() {
        return user.score;
    }

    public void setTotalScore(int totalScore) {
        user.score = totalScore;
    }
}
