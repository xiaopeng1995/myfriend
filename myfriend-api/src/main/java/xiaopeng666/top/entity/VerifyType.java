package xiaopeng666.top.entity;

/**
 * Verify Type
 */
public enum VerifyType {
    FINDPWD(1),
    REGISTER(2);

    private final int value;

    VerifyType(int value) {
        this.value = value;
    }

    public static VerifyType valueOf(int value) {
        for (VerifyType t : values()) {
            if (t.value == value) {
                return t;
            }
        }
        throw new IllegalArgumentException("invalid verify type " + value);
    }

    public int value() {
        return value;
    }
}
