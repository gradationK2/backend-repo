package core.backend.dto.review;

import core.backend.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewDto {
    private Long reviewId;
    private Long foodId;
    private String content;
    private int spicyLevel; //1~5단계

    //엔티티에서 DTO로 변환하는 메서드(foodservice오류나서..)
    public static ReviewDto fromEntity(Review review){
        return new ReviewDto(
                review.getId(),
                review.getFood().getId(),
                review.getContent(),
                review.getSpicyLevel()
        );
    }
}
