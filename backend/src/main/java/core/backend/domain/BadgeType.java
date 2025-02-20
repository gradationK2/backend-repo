package core.backend.domain;

import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum BadgeType {
    REVIEW_0("후기0개", 0),
    REVIEW_1("후기1개", 1),
    REVIEW_10("후기10개", 10),
    REVIEW_15("후기15개", 15),
    REVIEW_20("후기20개", 20),
    REVIEW_30("후기30개", 30),
    REVIEW_40("후기40개", 40),
    REVIEW_50("후기50개", 50),
    REVIEW_60("후기60개", 60),
    REVIEW_70("후기70개", 70),
    REVIEW_80("후기80개", 80),
    REVIEW_150("후기150개", 150);

    private final String label;
    private final int reviewCount;
    BadgeType(String label, int reviewCount) {
        this.label = label;
        this.reviewCount = reviewCount;
    }
    /**
     * 리뷰 개수에 따라 적절한 BadgeType을 반환합니다.
     * @param reviewCount 사용자가 작성한 리뷰 개수
     * @return 해당 리뷰 개수에 맞는 BadgeType
     */
    public static BadgeType findByReviewCount(int reviewCount) {
        BadgeType[] badges = BadgeType.values();
        for (int i = badges.length - 1; i >= 0; i--) {
            if (reviewCount >= badges[i].getReviewCount()) {
                return badges[i];
            }
        }
        throw new CustomException(ErrorCode.INVALID_BADGE_WORKING);
    }
}

/**
 * 기준 배찌 모양 예시
 * 후기 1개 올리면 B 배찌(비기너)
 * 후기 10개 올리면 조미료모양 배찌
 * 후기 15개 올리면 K 조각 1 배찌
 * 후기 20개 올리면 K 조각 2배찌
 * 후기 30개 올리면 음식점(모형) 배찌
 * 후기 40개 올리면 K 조각 3 배찌
 * 후기 50개 올리면 전문가 배찌
 * 후기 60개 올리면 K 조각 4 배찌
 * 후기 70개 올리면 K 조각 5배찌
 * 후기 80개 올리면 K 조각 6배찌
 * 후기 150개 올리면 우리 서버 아이콘배찌
 */