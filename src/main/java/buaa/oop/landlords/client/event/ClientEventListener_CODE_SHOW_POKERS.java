package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class ClientEventListener_CODE_SHOW_POKERS extends ClientEventListener{

    @Override
    /**
     * @param data includes currentPlayerNickname,currentPlayerId and used pokers,nextPlayerNickname,nextPlayerId
     */
    public void call(Channel channel, String data) {
        Map<String,Object>showPokers= JsonUtil.fromJson(data,new TypeReference<Map<String,Object>>(){});
        List<Poker> pokers= (List<Poker>) showPokers.get("pokers");
        SimplePrinter.printNotice("["+showPokers.get("currentPlayerNickname")+"]"+"used ");
        SimplePrinter.printPokers(pokers);
        if(User.getINSTANCE().getId()==(int)showPokers.get("nextPlayerId")){
            SimplePrinter.printNotice("It's your turn");
        }else{
            SimplePrinter.printNotice("It's"+showPokers.get("nextPlayerNickname")+"'s turn");
        }
    }
}
