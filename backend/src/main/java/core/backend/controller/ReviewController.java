package core.backend.controller;

import java.net.URI;
import java.util.*;
import core.backend.domain.Food;
import core.backend.domain.Member;
import core.backend.domain.Review;
import core.backend.dto.review.*;
import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;
import core.backend.service.FoodService;
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
public class ReviewController {
    private final ReviewService reviewService;
    private final FoodService foodService;
    private final MemberService memberService;

    @GetMapping("/users")
    public ResponseEntity<?> getReviews() {
        return ResponseEntity.ok().body(reviewService.getReviews());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getReviews(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(reviewService.getReviewDTOsByUser(userId));
    }

    @GetMapping("food/{foodId}")
    public ResponseEntity<?> getReviewByFood(@PathVariable("foodId") Long foodId){
        //음식 존재 확인
        Food food = foodService.findFoodByID(foodId);
        if(food == null){
            log.error("음식 조회 실패: foodId={}", foodId);
            throw new CustomException(ErrorCode.FOOD_NOT_FOUND);
        }

        //특정 음식에 대한 리뷰 조회
        List<Review> reviews = reviewService.getReviewsByFood(foodId);

        return ResponseEntity.ok().body(Map.of(
                "message", "특정 음식 리뷰 조회 성공",
                "reviews", reviews
        ));
    }

    @PostMapping
    public ResponseEntity<?> addReview(@Valid @ModelAttribute ReviewFormRequest request) { // TODO : MoedelAttribute <--> RequestBody
//        request.validate(); //요청데이터 확인
        
        Member member = memberService.getUser(request.getUserId());
        Food food = foodService.findFoodByID(request.getFoodId());

        //food가 정상적으로 조회되지 않으면 예외
        if(food == null){
            log.error("음식 조회 실패: foodID={}", request.getFoodId());
            throw new CustomException(ErrorCode.FOOD_NOT_FOUND);
        }

        Review review = reviewService.createReview(food, member, request.getContent(), request.getSpicyLevel());
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            reviewService.saveNewImage(request.getImage(), review);
        }
        int reviewCount = reviewService.getReviewsByUser(member.getId()).size();
        memberService.updateBadge(member, reviewCount);

        return ResponseEntity.created(URI.create("/reviews/users/" + member.getId()))
                .body(Map.of("message", "후기 작성 완료"));
    }

    @PutMapping
    public ResponseEntity<?> updateReview(@Valid @RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(request.getReviewId(), request.getContent(), request.getSpicyLevel());
        return ResponseEntity.ok().body(Map.of("message","후기 수정 완료"));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteReview(@Valid @RequestBody ReviewDeleteRequest request) {
        //리뷰 삭제 실행
        Review review = reviewService.getReviewByReview(request.getReviewId());
        Member member = review.getMember();
        reviewService.deleteReview(request.getReviewId());
        //전체 리뷰 리스트 갱신
        List<ReviewWithImagesDto> allReviews = reviewService.getReviews();
        //사용자의 리뷰 갱신
        int reviewCount = reviewService.getReviewsByUser(member.getId()).size();
        memberService.updateBadge(member, reviewCount);

        return ResponseEntity.ok().body(Map.of(
                "message","후기 삭제 완료",
                "allReviews", allReviews));
    }

    @DeleteMapping("/del-all")
    public ResponseEntity<?> deleteAllReview(@Valid @RequestBody ReviewDeleteAllRequest request) {
        Member member = memberService.getUser(request.getUserId());

        reviewService.deleteAllReview(member);

        int reviewCount = reviewService.getReviewsByUser(member.getId()).size();
        memberService.updateBadge(member, reviewCount);

        return ResponseEntity.ok().body(Map.of("message","후기 전체 삭제 완료"));
    }
}