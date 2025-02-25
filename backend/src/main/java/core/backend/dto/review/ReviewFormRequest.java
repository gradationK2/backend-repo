package core.backend.dto.review;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Slf4j
public class ReviewFormRequest {
    @JsonAlias({"food_id", "foodId"}) //여러 개의 키를 매핑 가능하게
    @NotNull(message = "음식이 선택되지 않았습니다.")
    @Min(1)
    private Long foodId;

    @JsonAlias({"user_id", "userId"})
    @NotNull(message = "회원이 선택되지 않았습니다.")
    @Min(1)
    private Long userId;

    @NotBlank(message = "빈 내용입니다.")
    private String content;

    @NotNull(message = "매운맛 단계를 선택해주세요.")
    @Min(value = 1, message = "매운맛 단계는 최소 1 이상이어야합니다.")
    private Integer spicyLevel;

    private MultipartFile image;
    public void validate(){
        if (!this.image.isEmpty()){
            log.info("DTO검증: userId={}, foodId={}, content={}, spicyLevel={}, image's size={}",
                    userId, foodId, content, spicyLevel, image.getSize());
        } else {
            log.info("DTO검증: userId={}, foodId={}, content={}, spicyLevel={}, image's size=0",
                    userId, foodId, content, spicyLevel);
        }
        log.info("{foodId}");
    }
}
