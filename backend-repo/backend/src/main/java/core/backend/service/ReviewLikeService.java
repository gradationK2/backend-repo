package core.backend.service;

import java.util.Optional;
import core.backend.domain.Member;
import core.backend.domain.Review;
import core.backend.domain.ReviewLike;
import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;
import core.backend.repository.ReviewLikeRepository;
import core.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;

    public void addUpvote(Review review, Member member) {
        Optional<ReviewLike> existingVote = reviewLikeRepository.findByReviewAndMember(review, member);

        if (existingVote.isEmpty()) {     // 이전에 뭔가를 누르지 않음
            reviewLikeRepository.save(ReviewLike.builder()
                    .review(review)
                    .member(member)
                    .liked(true)
                    .build());

            review.setUpvote(Optional.ofNullable(review.getUpvote())
                    .map(count -> count + 1)
                    .orElse(1));
            reviewRepository.save(review);
        } else {
            ReviewLike reviewLike = existingVote.get();
            throw new CustomException(reviewLike.isLiked()
                    ? ErrorCode.ALREADY_LIKED : ErrorCode.ALREADY_UNLIKED);
        }
    }

    public void addDownvote(Review review, Member member) {
        Optional<ReviewLike> existingVote = reviewLikeRepository.findByReviewAndMember(review, member);

        if (existingVote.isEmpty()) {     // 이전에 뭔가를 누르지 않음
            reviewLikeRepository.save(ReviewLike.builder()
                    .review(review)
                    .member(member)
                    .liked(false)
                    .build());

            review.setDownvote(Optional.ofNullable(review.getDownvote())
                    .map(count -> count + 1)
                    .orElse(1));
            reviewRepository.save(review);
        } else {
            ReviewLike reviewLike = existingVote.get();
            throw new CustomException(reviewLike.isLiked()
                    ? ErrorCode.ALREADY_LIKED : ErrorCode.ALREADY_UNLIKED);
        }
    }

    public void deleteUpvote(Review review, Member member) {
        ReviewLike reviewLike = reviewLikeRepository.findByReviewAndMember(review, member)
                .orElseThrow(() -> new CustomException(ErrorCode.UNLIKED_LIKED_NOT_FOUND));

        if (!reviewLike.isLiked())
            throw new CustomException(ErrorCode.LIKED_NOT_FOUND);

        review.setUpvote(Optional.ofNullable(review.getUpvote())
                .filter(upvote -> upvote > 0)
                .map(upvote -> upvote - 1)
                .orElse(0));

        reviewLikeRepository.delete(reviewLike);
        reviewRepository.save(review);
    }


    public void deleteDownvote(Review review, Member member) {
        ReviewLike reviewLike = reviewLikeRepository.findByReviewAndMember(review, member)
                .orElseThrow(() -> new CustomException(ErrorCode.UNLIKED_LIKED_NOT_FOUND));

        if (reviewLike.isLiked())
            throw new CustomException(ErrorCode.UNLIKED_NOT_FOUND);

        review.setDownvote(Optional.ofNullable(review.getDownvote())
            .filter(downvote -> downvote > 0)
            .map(downvote -> downvote - 1)
            .orElse(0));

        reviewLikeRepository.delete(reviewLike);
        reviewRepository.save(review);
    }
}
