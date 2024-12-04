package buaa.oop.landlords.client.event;

import buaa.oop.landlords.common.print.SimplePrinter;
import io.netty.channel.Channel;

/**
 * 无下一个状态
 */
public class ClientEventListener_CODE_GAME_LANDLORD_CYCLE extends ClientEventListener{
    @Override
    /**
     * 仅输出无人抢地主
     */
    public void call(Channel channel, String data) {
        SimplePrinter.printNotice("No one robbed the landlord");
    }
}
