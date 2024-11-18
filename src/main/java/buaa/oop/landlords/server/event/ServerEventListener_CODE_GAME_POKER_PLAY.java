package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.entities.PokerSell;
import buaa.oop.landlords.common.entities.Room;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.SellType;
import buaa.oop.landlords.common.enums.ClientRole;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *下一个状态为：ClientEventListener_CODE_SHOW_POKERS
 *       同时：若玩家牌出完 ClientEventListener_CODE_GAME_OVER
 *            否则进入 ServerEventListener_CODE_GAME_POKER_PLAY_REDIRECT
 */
@Slf4j
public class ServerEventListener_CODE_GAME_POKER_PLAY extends  ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        Map<String, Object> map = MapUtil.parse(data);

        List<Poker> currentPokers = (List<Poker>) map.get("poker");

        PokerSell currentPokerSell = (PokerSell) map.get("pokerSell");

        Room room = ServerContainer.getRoom(clientEnd.getRoomId());

        if (room == null) {
            return;
        }

        ClientEnd next = clientEnd.getNext();

        clientEnd.addRound();

        if (currentPokerSell.getSellType() == SellType.BOMB || currentPokerSell.getSellType() == SellType.KING_BOMB) {
            room.increaseRate();
        }

        room.setLastSellClient(clientEnd.getId());
        room.setLastPokerShell(currentPokerSell);
        room.setCurrentSellClient(next.getId());

        List<List<Poker>> lastPokerList = new ArrayList<>();
        for(int i = 0; i < room.getClientEndList().size(); i++){
            if(room.getClientEndList().get(i).getId() != clientEnd.getId()){
                lastPokerList.add(room.getClientEndList().get(i).getPokers());
            }
        }

        clientEnd.getPokers().removeAll(currentPokers);
        MapUtil mapUtil = MapUtil.newInstance()
                .put("clientId", clientEnd.getId())
                .put("clientNickname", clientEnd.getNickname())
                .put("clientRole", clientEnd.getRole())
                .put("pokers", currentPokers)
                .put("lastSellClientId", clientEnd.getId())
                .put("lastSellPokers", currentPokers);
        if (!clientEnd.getPokers().isEmpty()) {
            mapUtil.put("sellClientNickname", next.getNickname());
        }

        String result = mapUtil.json();

        for (ClientEnd client : room.getClientEndList()) {
            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_SHOW_POKERS, result);

        }

        if (!clientEnd.getPokers().isEmpty()) {
            ServerEventListener.get(ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT).call(next, result);
            return;
        }

        gameOver(clientEnd, room);
    }

    private void gameOver(ClientEnd winner, Room room) {
        ClientRole winnerType = winner.getRole();
        if (isSpring(winner, room)) {
            room.increaseRate();
        }

        setRoomClientScore(room, winnerType);

        ArrayList<Object> clientScores = new ArrayList<>();
        for (ClientEnd client : room.getClientEndList()) {
            MapUtil score = MapUtil.newInstance()
                    .put("clientId", client.getId())
                    .put("nickName", client.getNickname())
                    .put("score", client.getScore())
                    .put("scoreInc", client.getScoreInc())
                    .put("pokers", client.getPokers());
            clientScores.add(score.map());
        }

        SimplePrinter.ServerLog(clientScores.toString());
        String result = MapUtil.newInstance()
                .put("winnerNickname", winner.getNickname())
                .put("winnerType", winner.getRole())
                .put("scores", clientScores)
                .json();

        ServerEventListener.get(ServerEventCode.CODE_CLIENT_EXIT).call(winner, "");

    }

    private void setRoomClientScore(Room room, ClientRole winnerType) {
        int landLordScore = room.getScore() * 2;
        int peasantScore = room.getScore();
        if (winnerType == ClientRole.LANDLORD) {
            peasantScore = -peasantScore;
        } else {
            landLordScore = -landLordScore;
        }
        for (ClientEnd client : room.getClientEndList()) {
            if (client.getRole() == ClientRole.LANDLORD) {
                client.addScore(landLordScore);
            } else {
                client.addScore(peasantScore);
            }
        }
    }

    private boolean isSpring(ClientEnd winner, Room room) {
        boolean isSpring = true;
        for (ClientEnd client: room.getClientEndList()) {
            if (client.getRole() == winner.getRole()) {
                continue;
            }
            if (client.getRole() == ClientRole.PEASANT && client.getRound() > 0) {
                isSpring = false;
            }
            if (client.getRole() == ClientRole.LANDLORD && client.getRound() > 1) {
                isSpring = false;
            }
        }
        return isSpring;
    }
}
