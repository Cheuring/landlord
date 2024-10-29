package buaa.oop.landlords.common.enums;

public enum ServerEventCode {
    CODE_CLIENT_HEART_BEAT("心跳"),
    CODE_CLIENT_OFFLINE("玩家下线"),
    CODE_CLIENT_NICKNAME_SET("玩家设置昵称");

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
