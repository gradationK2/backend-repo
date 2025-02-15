package core.backend.controller;

import java.util.Map;
import core.backend.domain.Member;
import core.backend.domain.Review;
import core.backend.dto.review.ReviewVoteRequest;
import core.backend.service.MemberService;
import core.backend.service.ReviewLikeService;
import core.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reviews")
public class ReviewLikeController {
    private final ReviewService reviewService;
    private final MemberService memberService;
    private final ReviewLikeService reviewLikeService;

    @PostMapping("/upvote")
    public ResponseEntity<?> addUpvote(@Valid @RequestBody ReviewVoteRequest request) {
        Review review = reviewService.getReviewByReview(request.getReviewId());
        Member member = memberService.getUser(request.getMemberId());
        reviewLikeService.addUpvote(review, member);
        return ResponseEntity.ok().body(Map.of("message", "리뷰 좋아요 추가 완료"));
    }

    @PostMapping("/downvote")
    public ResponseEntity<?> addDownvote(@Valid @RequestBody ReviewVoteRequest request) {
        Review review = reviewService.getReviewByReview(request.getReviewId());
        Member member = memberService.getUser(request.getMemberId());
        reviewLikeService.addDownvote(review, member);
        return ResponseEntity.ok().body(Map.of("message", "리뷰 싫어요 추가 완료"));
    }

    @DeleteMapping("/upvote")
    public ResponseEntity<?> removeUpvote(@Valid @RequestBody ReviewVoteRequest request) {
        Review review = reviewService.getReviewByReview(request.getReviewId());
        Member member = memberService.getUser(request.getMemberId());
        reviewLikeService.deleteUpvote(review, member);
        return ResponseEntity.ok().body(Map.of("message", "리뷰 좋아요 삭제 완료"));
    }

    @DeleteMapping("/downvote")
    public ResponseEntity<?> removeDownvote(@Valid @RequestBody ReviewVoteRequest request) {
        Review review = reviewService.getReviewByReview(request.getReviewId());
        Member member = memberService.getUser(request.getMemberId());
        reviewLikeService.deleteDownvote(review, member);
        return ResponseEntity.ok().body(Map.of("message", "리뷰 싫어요 삭제 완료"));
    }
}