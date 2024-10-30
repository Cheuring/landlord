package buaa.oop.landlords.common.enums;

public enum ClientEventCode {
    CODE_CLIENT_CONNECT("客户端加入成功"),
    CODE_CLIENT_NICKNAME_SET("设置昵称"),
    CODE_SHOW_OPTIONS("显示选项");

    private final String msg;

    private ClientEventCode(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
