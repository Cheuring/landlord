package buaa.oop.landlords.client.event;

import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static buaa.oop.landlords.common.print.SimplePrinter.BLUE;

/**
 *
 */
public class ClientEventListener_CODE_CHAT extends ClientEventListener{
    @Override
    /**
     * @param data includes
     */
    public void call(Channel channel, String data) {
        Map<String,Object>chatMsg = JsonUtil.fromJson(data,new TypeReference<Map<String,Object>>(){});

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = now.format(formatter);
        String format="[%s] [%s]:%s\n";
        SimplePrinter.printChatMsg(format,BLUE,formattedTime,(String)chatMsg.get("ClientFrom"),(String)chatMsg.get("Content"));
    }

}
