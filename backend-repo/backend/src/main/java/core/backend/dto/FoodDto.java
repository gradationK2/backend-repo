package core.backend.dto;

import core.backend.domain.Food;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FoodDto {
    private Long foodId;
    private String name;
    private String imgUrl;

    public static FoodDto fromEntity(Food food) {
        return FoodDto.builder()
                .foodId(food.getId())
                .name(food.getName())
                .imgUrl(food.getImgUrl())
                .build();
    }
}
