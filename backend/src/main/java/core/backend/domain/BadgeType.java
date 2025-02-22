package core.backend.domain;

import core.backend.exception.CustomException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import core.backend.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum BadgeType {
    REVIEW_0("후기0개", 0, "/images/badges/badge_0.png"),
    REVIEW_1("후기1개", 1, "/images/badges/badge_1.png"),
    REVIEW_10("후기10개", 10, "/images/badges/badge_10.png"),
    REVIEW_15("후기15개", 15, "/images/badges/badge_15.png"),
    REVIEW_20("후기20개", 20, "/images/badges/badge_20.png"),
    REVIEW_30("후기30개", 30, "/images/badges/badge_30.png"),
    REVIEW_40("후기40개", 40, "/images/badges/badge_40.png"),
    REVIEW_50("후기50개", 50, "/images/badges/badge_50.png"),
    REVIEW_60("후기60개", 60, "/images/badges/badge_60.png"),
    REVIEW_70("후기70개", 70, "/images/badges/badge_70.png"),
    REVIEW_80("후기80개", 80, "/images/badges/badge_80.png"),
    REVIEW_150("후기150개", 150, "/images/badges/badge_150.png");

    private final String label;
    private final int reviewCount;
    private final String imagePath;

    BadgeType(String label, int reviewCount, String imagePath) {
        this.label = label;
        this.reviewCount = reviewCount;
        this.imagePath = imagePath;
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
    // 리뷰 개수 받아서 ->
    public static List<BadgeType> getEarnedBadges(int reviewCount) {
        List<BadgeType> earnedBadges = new ArrayList<>();

        for (BadgeType badge : BadgeType.values()) {
            // REVIEW_0은 제외 (실제 배지가 아니므로)
            if (badge != BadgeType.REVIEW_0 && badge.getReviewCount() <= reviewCount) {
                earnedBadges.add(badge);
            }
        }
        // 리뷰 개수 기준으로 오름차순 정렬
        earnedBadges.sort(Comparator.comparingInt(BadgeType::getReviewCount));

        return earnedBadges;
    }
    public static Integer getBadgeCount(int reviewCount) {
        int badgeCount = 0;
        for (BadgeType badge : BadgeType.values()) {
            if (badge.getReviewCount() == 0)
                continue;
            if (badge.getReviewCount() <= reviewCount) {
                badgeCount++;
            }
        }
        return badgeCount;
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