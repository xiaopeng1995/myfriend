package xiaopeng666.top.entity;

/**
 * Event Type Description
 */
public enum Event_Type {

    ADD(1),
    UPDATE(2),
    DELETE(3),
    SIGN_IN(4),
    SIGN_UP(5);

    private final int value;

    Event_Type(int value) {
        this.value = value;
    }

    public static Event_Type valueOf(int value) {
        for (Event_Type d : values()) {
            if (d.value == value) {
                return d;
            }
        }
        throw new IllegalArgumentException("invalid event type" + value);
    }

    public int value() {
        return value;
    }
}
