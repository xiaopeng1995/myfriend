package xiaopeng666.top.entity;

/**
 * User Role
 */
public enum UserRole {
    ADMIN(1),//管理员
    PLAYER(2),//普通用户
    SANKE(3);//散客匿名用户


    private final int value;

    UserRole(int value) {
        this.value = value;
    }

    public static UserRole valueOf(int value) {
        for (UserRole r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("invalid user role: " + value);
    }

    public int value() {
        return value;
    }
}
