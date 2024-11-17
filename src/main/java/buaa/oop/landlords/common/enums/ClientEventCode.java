package buaa.oop.landlords.common.enums;

public enum ClientEventCode {
    CODE_CLIENT_CONNECT("客户端加入成功"),
    CODE_CLIENT_NICKNAME_SET("设置昵称"),
    CODE_SHOW_OPTIONS("显示选项"),
    CODE_EXIT("退出游戏"),
    CODE_ROOM_CREATE_SUCCESS("创建房间成功"),
    CODE_SHOW_ROOMS("展示房间列表"),
    CODE_ROOM_JOIN_SUCCESS("加入房间成功"),
    CODE_ROOM_JOIN_FAIL_BY_FULL("房间人数已满"),
    CODE_ROOM_JOIN_FAIL_BY_INEXIST("加入-房间不存在"),
    CODE_GAME_STARTING("开始游戏"),
    CODE_GAME_LANDLORD_ELECT("抢地主"),
    CODE_GAME_LANDLORD_CONFIRM("地主确认"),
    CODE_GAME_LANDLORD_CYCLE("地主一轮确认结束"),
    CODE_GAME_POKER_PLAY("出牌回合"),
    CODE_GAME_POKER_PLAY_REDIRECT("出牌重定向"),
    CODE_GAME_POKER_PLAY_PASS("不出"),
    CODE_SHOW_POKERS("展示出牌");
    private final String msg;

    private ClientEventCode(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
