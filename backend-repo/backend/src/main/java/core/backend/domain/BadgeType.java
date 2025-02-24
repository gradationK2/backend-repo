package core.backend.domain;

import lombok.Getter;

@Getter
public enum BadgeType {
    LEVEL_ZERO("아기배찌"),
    LEVEL_ONE("숟가락배찌"),
    LEVEL_TWO("수저세트배찌"),
    LEVEL_THREE("서버아이콘배찌");

    private final String label;
    BadgeType(String label) {
        this.label = label;
    }
    public String getLevel() {
        switch (this) {
            case LEVEL_ZERO: return "0";
            case LEVEL_ONE: return "1";
            case LEVEL_TWO: return "2";
            case LEVEL_THREE: return "3";
            default: throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
