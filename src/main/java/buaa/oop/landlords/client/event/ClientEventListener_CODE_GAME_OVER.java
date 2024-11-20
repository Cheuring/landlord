package buaa.oop.landlords.client.event;

import buaa.oop.landlords.client.entities.User;
import buaa.oop.landlords.common.entities.Poker;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 打印获胜信息，进入 ClientEventListener_CODE_EXIT
 */
public class ClientEventListener_CODE_GAME_OVER extends ClientEventListener {
    @Override
    /**
     * @param data include winner's id and nickname,and roomid
     */
    public void call(Channel channel, String data) {
        Map<String, Object> map = MapUtil.parse(data);
        SimplePrinter.printNotice("\nPlayer " + map.get("winnerNickname") + "[" + map.get("winnerType") + "]" + " won the game.");

        if (map.containsKey("scores")) {
            List<Map<String, Object>> scores = JsonUtil.fromJson((String) map.get("scores"), new TypeReference<List<Map<String, Object>>>() {
            });
            for (Map<String, Object> score : scores) {
                if (!Objects.equals(score.get("clientId"), User.getINSTANCE().getId())) {
                    SimplePrinter.printNotice(score.get("nickName").toString() + "'s rest poker is:");
                    List<Poker> p = JsonUtil.fromJson((String) score.get("pokers"), new TypeReference<List<Poker>>() {
                    });
                    SimplePrinter.printPokers(p);
                }
            }
            SimplePrinter.printNotice("\n");
            // print score
            for (Map<String, Object> score : scores) {
                String scoreInc = score.get("scoreInc").toString();
                String scoreTotal = score.get("score").toString();
                if (User.getINSTANCE().getId() != (int) score.get("clientId")) {
                    SimplePrinter.printNotice(score.get("nickName").toString() + "'s score is " + scoreInc + ", total score is " + scoreTotal);
                } else {
                    SimplePrinter.printNotice("Your score is " + scoreInc + ", total score is " + scoreTotal);
                }
            }

        } get(ClientEventCode.CODE_EXIT).call(channel, data);
    }
}
