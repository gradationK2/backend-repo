package core.backend.dto.review;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateRequest {
    @NotBlank(message = "빈 내용입니다.")
    private String content;

    @NotNull(message = "매운맛 단계를 선택해주세요.")
    @Min(value = 1, message = "매운맛 단계는 최소 1이상이어야 합니다.")
    private Integer spicyLevel;

}
