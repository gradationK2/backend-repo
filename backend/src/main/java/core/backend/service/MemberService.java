package core.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import core.backend.domain.BadgeType;
import core.backend.domain.Member;
import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;
import core.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final String UPLOAD_DIR = "/home/daun/profile-images/";

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

    public Map<String, String> getBadgeInfo(Member member, BadgeType badge) {
        Map<String, String> badgeInfo = new HashMap<>();
        int currentReviewCount = member.getReviews().size();
        int requiredReviewCount = requiredReviewCount(currentReviewCount);

        badgeInfo.put("userId", member.getId().toString());
        badgeInfo.put("userName", member.getName());
        badgeInfo.put("badgeName", badge.name());
        badgeInfo.put("badgeLabel", badge.getLabel());
        badgeInfo.put("badgeReviewCount", String.valueOf(badge.getReviewCount()));
        badgeInfo.put("badgeImagePath", badge.getImagePath());
        badgeInfo.put("currentCount", String.valueOf(currentReviewCount));
        badgeInfo.put("requiredCount", String.valueOf(requiredReviewCount));
        return badgeInfo;
    }

    public Integer getUserPercent(Long memberId){
        return memberRepository.getUserReviewPercentile(memberId);
    }





    // 사진 관련 메서드 (FIXME : 함수가 뒤죽박죽이네요 .. 시간되면 리팩토링 한 번 하겠습니다)
    // 이미지 처리를 위한 메소드 추가
    public String updateProfileImage(Member member, MultipartFile image) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            String savedPath = saveNewImage(image);
            deleteExistingImage(member.getPhotoUrl());

            member.setPhotoUrl(savedPath);
            memberRepository.save(member);
            return savedPath;
        } catch (IOException e) {
            throw new RuntimeException("이미지 처리 중 오류가 발생했습니다.", e);
        }
    }
    private String saveNewImage(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/profile-images/" + fileName;
    }

    private void deleteExistingImage(String currentPhotoUrl) {
        if (currentPhotoUrl != null) {
            try {
                String oldFilePath = UPLOAD_DIR + currentPhotoUrl.substring(currentPhotoUrl.lastIndexOf("/") + 1);
                Files.deleteIfExists(Paths.get(oldFilePath));
            } catch (IOException e) {
                log.warn("기존 이미지 삭제 중 오류 발생: {}", e.getMessage());
            }
        }
    }


}
