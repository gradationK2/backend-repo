package core.backend.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReviewDeleteAllRequest {
    @JsonProperty("user_id")
    @NotNull(message = "회원이 선택되지 않았습니다.")
    @Min(1)
    private Long userId;
}
