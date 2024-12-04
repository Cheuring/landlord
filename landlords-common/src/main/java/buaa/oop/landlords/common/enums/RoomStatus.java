package buaa.oop.landlords.common.enums;

public enum RoomStatus {
    WAIT("等待"),
    STARTING("开始");

    private String msg;


    RoomStatus(String msg) {
        this.msg = msg;
    }

    public final String getMsg() {
        return msg;
    }

    public final void setMsg(String msg) {
        this.msg = msg;
    }
}
