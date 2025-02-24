package core.backend.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ReviewVoteRequest {
    @NotNull(message = "리뷰 ID는 필수입니다")
    @JsonProperty("review_id")
    private Long reviewId;

    @NotNull(message = "사용자 ID는 필수입니다")
    @JsonProperty("user_id")
    private Long memberId;
}
