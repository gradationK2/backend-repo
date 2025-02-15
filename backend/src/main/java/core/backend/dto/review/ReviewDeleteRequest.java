package core.backend.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDeleteRequest {
    @JsonProperty("review_id")
    @NotNull(message = "리뷰가 선택되지 않았습니다.")
    @Min(1)
    private Long reviewId;
}
