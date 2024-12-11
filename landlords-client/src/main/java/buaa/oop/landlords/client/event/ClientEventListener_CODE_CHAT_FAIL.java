package buaa.oop.landlords.client.event;

import buaa.oop.landlords.common.print.SimplePrinter;
import io.netty.channel.Channel;

/**
 *
 */
public class ClientEventListener_CODE_CHAT_FAIL extends ClientEventListener{
    @Override
    public void call(Channel channel, String data) {

        SimplePrinter.printWarning("Client not exist");
    }
}
