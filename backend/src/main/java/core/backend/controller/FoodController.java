package core.backend.controller;

import core.backend.domain.Food;
import core.backend.dto.FoodDetailDto;
import core.backend.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*") // 외부 클라이언트에서도 접근 가능
public class FoodController {

    private final FoodService foodService;

    //음식 csv 파일 업로드, db저장
    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file")MultipartFile file) {
        foodService.saveFoodsFromCsv(file);
        return ResponseEntity.ok("CSV 데이터가 성공적으로 저장되었습니다.");
    }

    //전체 음식 리스트 조회(카테고리별 필터링, 정렬)
    @GetMapping
    public ResponseEntity<List<Food>> getFoods(
        @RequestParam(name = "category", required = false) String category,
        @RequestParam(name = "sort", required = false, defaultValue = "new") String sort){

        List<Food> foods = foodService.getFoods(category, sort);
        return ResponseEntity.ok(foods);
    }

    //음식 검색(이름, 카테고리)
    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFoods(@RequestParam(name = "query") String query){
        List<Food> searchResults = foodService.searchFoods(query);
        return ResponseEntity.ok(searchResults);
    }

    //음식 상세 조회(매운맛 비교 포함)
    @GetMapping("/detail/{foodId}")
    public ResponseEntity<FoodDetailDto> getFoodDetail(@PathVariable("foodId") Long foodId){
        return ResponseEntity.ok(foodService.getFoodDetail(foodId));
    }
}
