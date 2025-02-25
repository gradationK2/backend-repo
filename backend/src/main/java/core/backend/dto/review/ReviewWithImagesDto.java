package core.backend.dto.review;


import java.time.LocalDateTime;
import java.util.*;
import core.backend.domain.Review;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewWithImagesDto {
    private Long id;
    private Long foodId;
    private Long memberId;
    private String content;
    private Integer spicyLevel;
    private List<String> imageUrls;
    private LocalDateTime createDate;
    private Integer upvote;
    private Integer downvote;

    // 생성자, getter, setter 메서드들

    public static ReviewWithImagesDto fromEntity(Review review) {
        ReviewWithImagesDto dto = new ReviewWithImagesDto();
        dto.setId(review.getId());
        dto.setFoodId(review.getFood() != null ? review.getFood().getId() : null);
        dto.setMemberId(review.getMember() != null ? review.getMember().getId() : null);
        dto.setContent(review.getContent());
        dto.setSpicyLevel(review.getSpicyLevel());
        dto.setCreateDate(review.getCreateDate());
        dto.setUpvote(review.getUpvote());
        dto.setDownvote(review.getDownvote());

        String imgUrl = review.getImgUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            if (imgUrl.contains("|")) {
                dto.setImageUrls(Arrays.asList(imgUrl.split("\\|")));
            } else {
                dto.setImageUrls(Collections.singletonList(imgUrl));
            }
        } else {
            dto.setImageUrls(Collections.emptyList());
        }

        return dto;
    }
}