package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static buaa.oop.landlords.common.print.SimplePrinter.*;

/**
 *
 */
public class ClientEventListener_CODE_CHAT extends ClientEventListener{
    private static final String FROM = "\n(私聊) %s  来自 %s :\n%s\n";
    private static final String TO = "\n(私聊) %s  发给 %s :\n%s\n";
    private static final String ALL = "\n%s  %s :\n%s\n";
    @Override
    public void call(Channel channel, String data) {
        Map<String,Object>chatMsg = JsonUtil.fromJson(data,new TypeReference<Map<String,Object>>(){});

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = now.format(formatter);
        String clientFrom = (String) chatMsg.get("ClientFrom");
        String clientTo = (String) chatMsg.get("ClientTo");
        String content = (String) chatMsg.get("Content");

        if(clientTo.equalsIgnoreCase("ALL")){
            SimplePrinter.printChatMsg(ALL, WHITE, formattedTime, clientFrom, content);
        }else{
            if(clientTo.equals(User.INSTANCE.getNickname())){
                SimplePrinter.printChatMsg(FROM, PINK, formattedTime, clientFrom, content);
            }else{
                SimplePrinter.printChatMsg(TO, PINK, formattedTime, clientFrom, content);
            }
        }
    }
}
