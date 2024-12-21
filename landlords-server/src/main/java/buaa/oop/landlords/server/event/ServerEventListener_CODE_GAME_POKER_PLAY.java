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
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import buaa.oop.landlords.server.mapper.UserMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

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

        List<Poker> currentPokers = JsonUtil.fromJson((String)map.get("poker"), new TypeReference<List<Poker>>(){});

        PokerSell currentPokerSell = JsonUtil.fromJson((String)map.get("pokerSell"), new TypeReference<PokerSell>(){});

        Room room = ServerContainer.getRoom(clientEnd.getRoomId());

        if (room == null) {
            return;
        }

        ClientEnd next = ServerContainer.getClient(clientEnd.getNext());

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
                .put("currentPlayerNickname", clientEnd.getNickname())
                .put("role", clientEnd.getRole())
                .put("pokers", currentPokers)
                .put("lastSellClientId", clientEnd.getId())
                .put("lastSellClientName", clientEnd.getNickname())
                .put("lastSellPokers", currentPokers);
        if (!clientEnd.getPokers().isEmpty()) {
            mapUtil.put("nextPlayerNickname", next.getNickname());
            mapUtil.put("nextPlayerId", next.getId());
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
        SqlSession session = null;
        int isSuccessful = 0;
        try {
            session = ServerContainer.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);

            for (ClientEnd client : room.getClientEndList()) {

                int res = mapper.updateUserScore(client.getId(), client.getScore());
                if (res == 0) {
                    throw new Exception("client " + client.getNickname() + " update failed");
                }

                MapUtil score = MapUtil.newInstance()
                        .put("clientId", client.getId())
                        .put("nickName", client.getNickname())
                        .put("score", client.getScore())
                        .put("scoreInc", client.getScoreInc())
                        .put("pokers", client.getPokers());
                clientScores.add(score.map());
            }

            for(ClientEnd client: room.getClientEndList()){
                client.setTotalScore(client.getTotalScore() + client.getScore());
            }

            isSuccessful = 1;
        }catch (Exception e){
            log.error("Failed to update score", e);
            if(session != null){
                session.rollback();
            }
        }finally {
            if(session != null){
                session.commit();
                session.close();
            }
        }

        SimplePrinter.ServerLog(clientScores.toString());
        String result = MapUtil.newInstance()
                .put("status", isSuccessful)
                .put("winnerNickname", winner.getNickname())
                .put("winnerType", winner.getRole())
                .put("scores", clientScores)
                .json();

        ServerEventListener.get(ServerEventCode.CODE_CLIENT_EXIT).call(winner, result);

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
