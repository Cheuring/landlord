package buaa.oop.landlords.common.entities;

import buaa.oop.landlords.common.enums.ClientRole;
import buaa.oop.landlords.common.enums.ClientStatus;
import io.netty.channel.Channel;
import lombok.Data;

import java.util.List;

@Data
public class ClientEnd {
    private int id;

    private String nickName;

    private List<Poker> pokers;

    private ClientRole role;

    private ClientEnd pre, next;

    private Channel channel;

    private ClientStatus status;

    private int roomId;

    public ClientEnd(int id, Channel channel, ClientStatus status) {
        this.id = id;
        this.channel = channel;
        this.status = status;
    }
}
