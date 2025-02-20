package core.backend.service;

import core.backend.domain.BadgeType;
import core.backend.domain.Member;
import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;
import core.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member getUser(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public void updateBadge(Member member, int reviewCount) {
        if (reviewCount == 1 || reviewCount == 15 || reviewCount % 10 == 0) {
            BadgeType updated = BadgeType.findByReviewCount(reviewCount);
            member.setBadge(updated);
            memberRepository.save(member);
            log.info("{}의 배지 : {} (작성 글 개수 :{})", member.getName(), member.getBadge(), reviewCount);
        }
    }

    public int requiredReviewCount(int currentReviews) {
        int[] badgeMilestones = {1, 10, 15, 20, 30, 40, 50, 60, 70, 80, 150};
        if (currentReviews >= 150) {
            return 0;
        }
        for (int milestone : badgeMilestones) {
            if (currentReviews < milestone) {
                return milestone - currentReviews;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BADGE_WORKING);
    }
}
