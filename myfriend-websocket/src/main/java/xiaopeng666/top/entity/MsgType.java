package xiaopeng666.top.entity;

/**
 * 消息的类型
 * MsgType enums
 */
public enum MsgType {

    MSMINFO(1),
    MSMCOUNT(2);

    private final int value;

    MsgType(int value) {
        this.value = value;
    }

    public static MsgType valueOf(int value) {
        for (MsgType t : values()) {
            if (t.value == value) {
                return t;
            }
        }
        throw new IllegalArgumentException("invalid Msg status: " + value);
    }

    public int value() {
        return value;
    }
}
