package buaa.oop.landlords.common.enums;

public enum ServerEventCode {
    CODE_CLIENT_HEART_BEAT("心跳"),
    CODE_CLIENT_OFFLINE("玩家下线"),
    CODE_CLIENT_NICKNAME_SET("玩家设置昵称"),
    CODE_ROOM_CREATE("创建房间"),
    CODE_ROOM_GETALL("获取房间列表"),
    CODE_ROOM_JOIN("加入房间"),
    CODE_GAME_STARTING("开始游戏"),
    CODE_GAME_LANDLORD_ELECT("抢地主"),
    CODE_GAME_POKER_PLAY("出牌回合"),
    CODE_GAME_POKER_PLAY_REDIRECT("出牌重定向"),
    CODE_GAME_POKER_PLAY_PASS("不出"),
    CODE_CLIENT_EXIT("退出游戏"),
    CODE_CHAT("聊天");

    private String msg;

    ServerEventCode(String msg) {
        this.msg = msg;
    }

    public final String getMsg() {
        return msg;
    }

    public final void setMsg(String msg) {
        this.msg = msg;
    }

}
