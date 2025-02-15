package core.backend.controller;

import core.backend.domain.Food;
import core.backend.domain.Heart;
import core.backend.domain.Member;
import core.backend.dto.FoodDto;
import core.backend.dto.MemberLikeFoodRequest;
import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;
import core.backend.service.FoodService;
import core.backend.service.HeartService;
import core.backend.service.MemberService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HeartController {
    private final HeartService heartService;
    private final MemberService memberService;
    private final FoodService foodService;

    @GetMapping("/users/likes/{userId}")
    public List<FoodDto> getFoods(@PathVariable("userId") Long userId) {
        Member member = memberService.getUser(userId);
        List<Heart> hearts = heartService.getHeartsByUser(member);
        List<Food> foods = hearts.stream().map(Heart::getFood).toList();
        return foods.stream().map(FoodDto::fromEntity).toList();
    }

    @GetMapping("/heart/{foodId}")
    public ResponseEntity<?> getSizeOfHearts(@PathVariable("foodId") Long foodId){
        Food food = foodService.findFoodByID(foodId);
        int size = heartService.getHeartsByFood(food).size();
        return ResponseEntity.ok(Map.of("heartCount", String.valueOf(size)));
    }


    @GetMapping("/users/badge/{userId}")
    public Map<String, String> getBadge(@PathVariable("userId") Long userId) {
        Member member = memberService.getUser(userId);
        Map<String, String> badgeInfo = new HashMap<>();
        badgeInfo.put("badgeLevel", member.getBadge().getLevel());
        badgeInfo.put("userId", member.getId().toString());
        badgeInfo.put("userName", member.getName());
        badgeInfo.put("badgeName", member.getBadge().getLabel());
        badgeInfo.put("icon_url", ""); // TODO : 뱃지 경로
        return badgeInfo;
    }

    @PostMapping("/users/heart")
    public ResponseEntity<?> likeFoodByUser(@Valid @RequestBody MemberLikeFoodRequest request) {
        request.validate(); // 요청데이터 검증하려고 추가..
        // TODO : 데이터가 잘 안들어왔을 때 Valid에서 어떻게 에러 메시지 출력하나?
        Food food = foodService.findFoodByID(request.getFoodId());
        Member member = memberService.getUser(request.getUserId());

        if (heartService.isLiked(member, food)){
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        heartService.addUser(member, food);
        return ResponseEntity.ok().body(Map.of("message","좋아요 추가 완료"));
    }

    @Transactional // TODO : 리뷰 삭제할 때는 안 써도 삭제가 됐었는데 ?
    @DeleteMapping("/users/heart")
    public ResponseEntity<?> unlikeFoodByUser(@Valid @RequestBody MemberLikeFoodRequest request) {
        request.validate(); // 요청데이터 검증하려고 추가..
        Food food = foodService.findFoodByID(request.getFoodId());
        Member member = memberService.getUser(request.getUserId());

        if (!heartService.isLiked(member, food)){ // 좋아요를 누르지 않은 상태 -> 아무 것도 누르지 않은 경우
            throw new CustomException(ErrorCode.LIKED_NOT_FOUND);
        }

        heartService.deleteUser(member, food);
        return ResponseEntity.ok().body(Map.of("message","좋아요 취소 완료"));
    }
}
