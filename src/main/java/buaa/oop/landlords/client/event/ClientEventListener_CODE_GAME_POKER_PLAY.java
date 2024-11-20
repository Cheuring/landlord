package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.entities.PokerSell;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.PokerLevel;
import buaa.oop.landlords.common.enums.SellType;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.print.SimpleWriter;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.common.utils.PokerUtil;
import buaa.oop.landlords.server.ServerContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *等待输入：
 * 若输入无效 重新调用该状态
 * 若有效 pass 进入 ServerEventListener_CODE_GAME_POKER_PLAY_PASS
 *       exit 进入 ServerEventListener_CODE_CLIENT_EXIT
 *       view 重新打印出牌信息,重新调用该状态
 *       出牌合法 进入 ServerEventListener_CODE_GAME_POKER_PLAY
 *          String result = MapUtil.newInstance()
 *                 .put("pokers", clientEnd.getPokers())
 *                 .put("lastSellPokers", dataMap.get("lastSellPokers"))
 *                 .put("lastSellClientId", dataMap.get("lastSellClientId"))
 *                 .put("clientInfos", clientInfos)
 *                 .put("sellClientId", room.getCurrentSellClient())
 *                 .put("sellClientNickname", ServerContainer.CLIENT_END_MAP.get(room.getCurrentSellClient()).getNickname())
 *                 .json();
 */
public class ClientEventListener_CODE_GAME_POKER_PLAY extends ClientEventListener{
    @Override
    /**
     * @param data includes last player's pokers used and his id,all players info, current player's id and nickname
     */
    public void call(Channel channel, String data) {
        Map<String, Object>roominfo= MapUtil.parse(data);
        List<Map<String, Object>>clientInfo= JsonUtil.fromJson((String)roominfo.get("clientInfos"),new TypeReference<ArrayList<Map<String, Object>>>(){});
        SimplePrinter.printNotice("It's your turn to play");
        List<Poker> pokers = JsonUtil.fromJson((String)roominfo.get("pokers"), new TypeReference<List<Poker>>(){});
        List<Poker> lastPokers = JsonUtil.fromJson((String)roominfo.get("lastSellPokers"), new TypeReference<List<Poker>>(){});
        String lastSellClientNickname = (String) roominfo.get("lastSellClientName");
        Integer lastSellClientId = (Integer) roominfo.get("lastSellClientId");
        String lastSellClientType = null;
        PokerSell lastPokerSell = PokerUtil.checkPokerSell(lastPokers);
        List<PokerSell> sells = PokerUtil.validSells(lastPokerSell, pokers);
        for (Map<String, Object> clientEnd : clientInfo) {
            if (Objects.equals((Integer) clientEnd.get("clientId"), lastSellClientId)) {
                lastSellClientType = (String) clientEnd.get("role");
                break;
            }
        }
        printInfo(roominfo,pokers);

        if(sells.isEmpty() && (Integer) roominfo.get("lastSellClientId") != User.INSTANCE.getId()) {
            SimplePrinter.printNotice("You don't have winning combination.");
            pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_PASS, data);
            return;
        }

        String userInput = SimpleWriter.write(User.INSTANCE.getNickname(), "combination");
        if (userInput == null) {
            SimplePrinter.printNotice("Invalid enter!");
            call(channel, data);
            return;
        }

        if(userInput.equalsIgnoreCase("exit")||userInput.equalsIgnoreCase("e")){
            pushToServer(channel, ServerEventCode.CODE_ROOM_EXIT);
            ClientEventListener.get(ClientEventCode.CODE_SHOW_OPTIONS).call(channel, null);
            return;
        }

        if(userInput.equalsIgnoreCase("pass")||userInput.equalsIgnoreCase("p")){
            if( (Integer) roominfo.get("lastSellClientId") == User.INSTANCE.getId()){
                SimplePrinter.printNotice("You played the previous card, so you can't pass again.");
                pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT, data);
            } else{
                pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_PASS, data);
            }
            return;
        }

        if (userInput.equalsIgnoreCase("view")||userInput.equalsIgnoreCase("v")) {
            call(channel, data);
            return;
        }

        userInput = userInput.trim();
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
            Character[] option = new Character[options.size()];
            option = options.toArray(option);
            int[] indexes = PokerUtil.getIndexes(option, pokers);
            if (!PokerUtil.checkPokerIndex(indexes, pokers)){
                SimplePrinter.printNotice("The combination is illegal");

                if (lastPokers != null) {
                    SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] played:");
                    SimplePrinter.printPokers(lastPokers);
                }
                pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT, data);
                return;
            }

                List<Poker> currentPokers = PokerUtil.getPoker(indexes, pokers);
                PokerSell currentPokerSell = PokerUtil.checkPokerSell(currentPokers);
                if(currentPokerSell.getSellType() == SellType.ILLEGAL ) {
                    SimplePrinter.printNotice("The combination is illegal!");

                if (lastPokers != null) {
                    SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] played:");
                    SimplePrinter.printPokers(lastPokers);
                }

                pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT, data);
                return;
            }

                if(  lastPokerSell.getSellType() != SellType.ILLEGAL && (Integer) roominfo.get("lastSellClientId") != User.INSTANCE.getId()){

                if((lastPokerSell.getSellType() != currentPokerSell.getSellType() || lastPokerSell.getPokers().size() != currentPokerSell.getPokers().size()) && currentPokerSell.getSellType() != SellType.BOMB && currentPokerSell.getSellType() != SellType.KING_BOMB){
                    SimplePrinter.printNotice(STR."Your combination is \{currentPokerSell.getSellType()} (\{currentPokerSell.getPokers().size()}), but the previous combination is \{lastPokerSell.getSellType()} (\{lastPokerSell.getPokers().size()}). Mismatch!");

                    if(lastPokers != null) {
                        SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] played:");
                        SimplePrinter.printPokers(lastPokers);
                    }

                    pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT, data);
                    return;
                }

                if(lastPokerSell.getScore() >= currentPokerSell.getScore()){
                    SimplePrinter.printNotice("Your combination has lower rank than the previous. You cannot play this combination!");

                    if(lastPokers != null) {
                        SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] played:");
                        SimplePrinter.printPokers(lastPokers);
                    }

                    pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT, data);
                    return;
                }
            }

                String result = MapUtil.newInstance()
                        .put("poker", currentPokers)
                        .put("pokerSell", currentPokerSell)
                        .json();
                pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY, result);
            } else {
                SimplePrinter.printNotice("Invalid enter!");
                if (lastPokers != null) {
                    SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] played:");
                    SimplePrinter.printPokers(lastPokers);
                }

            call(channel, data);
        }

    }
    public void printInfo(Map<String, Object> roominfo,List<Poker> pokers){
        SimplePrinter.printNotice("Last cards are:");
        SimplePrinter.printPokers(JsonUtil.fromJson((String)roominfo.get("lastSellPokers"), new TypeReference<List<Poker>>(){}));
        SimplePrinter.printNotice("Please enter the combination you came up with (enter [exit|e] to exit current room, enter [pass|p] to jump current round, enter [view|v] to show information before.)");
        SimplePrinter.printPokers(pokers);
    }


}
