package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.PokerLevel;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *等待输入：
 * 若输入无效 重新调用该状态
 * 若有效 pass 进入 ServerEventListener_CODE_GAME_POKER_PLAY_PASS
 *       exit 进入 ServerEventListener_CODE_CLIENT_EXIT
 *       view 重新打印出牌信息,重新调用该状态
 *       出牌合法 进入 ServerEventListener_CODE_GAME_POKER_PLAY
 */
public class ClientEventListener_CODE_GAME_POKER_PLAY extends ClientEventListener{
    @Override
    /**
     * @param data includes last player's pokers used and his id,all players info, current player's id and nickname
     */
    public void call(Channel channel, String data) {
        Map<String, Object>roominfo= MapUtil.parse(data);
        SimplePrinter.printNotice("It's your turn to play");
        List<Poker> pokers = JsonUtil.fromJson((String)roominfo.get("pokers"), new TypeReference<List<Poker>>(){});
        printInfo(roominfo,pokers);

        String userInput = SimpleWriter.write(User.INSTANCE.getNickname(), "combination");
        if (userInput == null) {
            SimplePrinter.printNotice("Invalid enter");
            call(channel, data);
        }
        else{
            if(userInput.equalsIgnoreCase("exit")||userInput.equalsIgnoreCase("e")){
                pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_PASS);
            }
            else if(userInput.equalsIgnoreCase("pass")||userInput.equalsIgnoreCase("p")){
               //todo  judgePassVaild();
                pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_PASS);
            }
            else if (userInput.equalsIgnoreCase("view")||userInput.equalsIgnoreCase("v")) {
                call(channel, userInput);
                return;
            }else{
                //todo judgeComibinationVaild
                String[] strs = userInput.split(" ");
                List<Character> options = new ArrayList<>();
                boolean access = true;
                for(int i=0;i<strs.length;i++){
                    String str = strs[i];
                    for(char c:str.toCharArray()){
                        if(c == ' '|| c == '\t'){
                        } else {
                            if(!PokerLevel.aliasContains(c)){
                                access = false;
                                break;
                            } else {
                                options.add(c);
                            }
                        }
                    }
                }
                if(access){

                } else {
                    SimplePrinter.printNotice("Invalid enter");
                    if (lastPokers != null) {
                        SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] played:");
                        SimplePrinter.printPokers(lastPokers);
                    }

                    call(channel, data);
                }
            }
        }
    }
    public void printInfo(Map<String, Object> roominfo,List<Poker> pokers){
        SimplePrinter.printNotice("Last cards are");
        SimplePrinter.printNotice(roominfo.containsKey("lastPokers")?roominfo.get("lastPokers").toString():"");
        SimplePrinter.printNotice("Please enter the combination you came up with (enter [exit|e] to exit current room, enter [pass|p] to jump current round, enter [view|v] to show information before)");
        SimplePrinter.printPokers(pokers);
    }
}
