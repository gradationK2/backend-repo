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
    REVIEW_50("후기50개", 50, "/images/badges/badge_50.png");

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