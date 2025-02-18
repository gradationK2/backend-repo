package core.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import core.backend.domain.Food;
import core.backend.dto.review.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodDetailDto {
    private String imgUrl;
    private String name;
    private String description;
    private String spicyLevelText; //사용자 후기 기반 매운맛
    private String spicinessComparison; //타바스코 소스 비교
    private List<ReviewDto> reviews; //리뷰 리스트
    private List<FoodDto> popularFoods; //가장 인기 있는 음식 리스트
    private Integer heartSize;
}
