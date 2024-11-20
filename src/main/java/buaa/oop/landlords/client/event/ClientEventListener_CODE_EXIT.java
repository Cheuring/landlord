package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.MapUtil;
import io.netty.channel.Channel;

import java.util.Map;

/**
 *
 */
public class ClientEventListener_CODE_EXIT extends ClientEventListener{
    @Override
    public void call(Channel channel, String data) {
        Map<String, Object> map = MapUtil.parse(data);

        Integer exitClientId = (Integer) map.get("exitClientId");

        String role = (String)map.get("exitClientNickname");
        if (exitClientId == User.getINSTANCE().getId()) {
            role = "You";
        }
        SimplePrinter.printNotice(String.format("\n%s left the room. Room disbanded!\n", role));
        get(ClientEventCode.CODE_SHOW_OPTIONS).call(channel, data);
    }
}
