package core.backend.service;

import java.util.List;
import java.util.Optional;

import core.backend.domain.Food;
import core.backend.domain.Member;
import core.backend.domain.Review;
import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;
import core.backend.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> getReviews() {
        return reviewRepository.findAll();
    }

    public Review getReviewByReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }

    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findAllByMemberId(userId);
    }

    @Transactional
    public Review createReview(Food food, Member member, String content, Integer spicyLevel) {
        //디버깅용 로그
        log.info("리뷰 생성 요청: food={}, member={}, content={}, spicyLevel={}",
                food != null ? food.getId() : "NULL",
                member != null ? member.getId() : "NULL",
                content, spicyLevel);

        //유효성 검사 추가
        if (food == null) {
            throw new CustomException(ErrorCode.FOOD_NOT_FOUND);
        }
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (content == null || content.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        if (spicyLevel == null) {
            spicyLevel = 1; //기본값
        } else if (spicyLevel < 1 || spicyLevel > 5) {
            throw new CustomException(ErrorCode.INVALID_INPUT);

        }

        Review review = Review.builder()
                .food(food)
                .member(member)
                .content(content)
                .spicyLevel(spicyLevel)
                .build();
        Review savedReview = reviewRepository.save(review);
        return savedReview;
    }

    public Review updateReview(Long reviewId, String content, Integer spicyLevel) {
        return reviewRepository.findById(reviewId)
                .map(review -> {
                    if (content != null && !content.trim().isEmpty()) {
                        review.setContent(content);
                    }
                    if (spicyLevel != null && spicyLevel >= 1 && spicyLevel <= 5) {
                        review.setSpicyLevel(spicyLevel);
                    }
                    return reviewRepository.save(review);
                }).orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }


    @Transactional
    public void deleteReview(Long reviewId) {
        //삭제 전에 리뷰 존재 여부 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.info("리뷰 찾을 수 없음: review_id={}", reviewId);
                    return new CustomException(ErrorCode.REVIEW_NOT_FOUND);
                });
        review.setFood(null);
        review.setMember(null);
        reviewRepository.save(review);

        reviewRepository.deleteById(reviewId);
        reviewRepository.flush();

        // 삭제 후 다시 확인
        Optional<Review> deletedReview = reviewRepository.findById(reviewId);
        if (deletedReview.isPresent()) {
            throw new RuntimeException("DELETE 실패: review_id=" + reviewId);
        } else {
            log.info("DELETE 성공: review_id={}", reviewId);
        }
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
}

