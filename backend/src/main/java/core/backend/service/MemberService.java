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
        BadgeType badge;
        switch (reviewCount / 5) {
            case 0:
                badge = BadgeType.LEVEL_ZERO;
                break;
            case 1:
                badge = BadgeType.LEVEL_ONE;
                break;
            case 2:
                badge = BadgeType.LEVEL_TWO;
                break;
            default:
                badge = BadgeType.LEVEL_THREE;
        }
        member.setBadge(badge);
        memberRepository.save(member);
        if (reviewCount % 5 == 0)
            log.info(String.format("%s의 배지 : %s (작성 글 개수 :%s)", member.getName(), member.getBadge(), reviewCount));
    }

    public int requiredReviewCount(int currentReviews) {
        int target = (currentReviews / 5 + 1) * 5;
        return target - currentReviews;
    }
}
