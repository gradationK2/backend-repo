package core.backend.service;

import core.backend.domain.Food;
import core.backend.domain.Review;
import core.backend.dto.FoodDetailDto;
import core.backend.dto.FoodDto;
import core.backend.dto.review.ReviewDto;
import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;
import core.backend.repository.FoodRepository;
import core.backend.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FoodService {

    private final FoodRepository foodRepository;
    private final ReviewRepository reviewRepository;

    private static final int TABASCO_SCOVILLE = 3750; //타바스코 평균 스코빌

    //특정 음식 엔티티 조회
    @Transactional
    public Food findFoodByID(Long foodId){
        return foodRepository.findById(foodId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOOD_NOT_FOUND));
    }

    //특정 음식 상세 조회(매운맛 비교 포함)
    @Transactional
    public FoodDetailDto getFoodDetail(Long foodId) {
        //fetch join적용(전:reviews와 hearts를 함께 가져오려 해서(??) MultipleBagFetchException 발생 -> 후: List형을 Set형으로 변경 혹은 쿼리 분리 등)
//        Food food = foodRepository.findByIdWithReviewsAndHearts(foodId)
//                .orElseThrow(() -> new CustomException(ErrorCode.FOOD_NOT_FOUND));
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOOD_NOT_FOUND));

        //음식 리뷰 가져오기(이미 fetch join으로 가져와서 추가 쿼리 발생 없음)
        List<ReviewDto> reviewDtos = food.getReviews().stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
//        List<Review> reviews = reviewRepository.findByFood(food);
//        List<ReviewDto> reviewDtos = reviews.stream()
//                .map(ReviewDto::fromEntity)
//                .collect(Collectors.toList());

        //평균 매운맛 계산(후기 기반)
        double avgSpicyLevel = food.getReviews().stream()
                .mapToInt(Review::getSpicyLevel) // 1~5단계 점수 가져옴
                .average()
                .orElse(0);

        String spicyLevelText = classifySpicyLevel(avgSpicyLevel);

        //타바스코 소스와 비교
        String spicinessComparison = compareSpiciness(food.getScoville());

        //가장 인기 있는 음식(좋아요 내림차순 정렬 기준 2개 가져오기)
        List<Food> top2MostLikedFoods = foodRepository.findTop2MostLikedFoods();
        List<FoodDto> popularFoods = top2MostLikedFoods
                .stream()
                .map(FoodDto::fromEntity)
                .collect(Collectors.toList());
        top2MostLikedFoods.forEach(t ->
                log.info("foodName: {}, foodId: {}, foodHeartSize: {}",
                        t.getName(), t.getId(), t.getHeartCount()));

        return new FoodDetailDto(
                food.getImgUrl(),
                food.getName(),
                food.getDescription(),
                spicyLevelText, // 후기 기반 매운맛
                spicinessComparison, // 타바스코와 비교 매운맛
                reviewDtos, // 리뷰 리스트
                popularFoods, // 인기 음식 리스트
                food.getHeartCount()
        );
    }
    
    //사용자 후기 기반 평균 매운맛 계산
    private String classifySpicyLevel(double avgSpicyLevel){
        if(avgSpicyLevel >= 4){
            return "매움";
        } else if (avgSpicyLevel >= 2) {
            return "보통";
        } else {
            return "안 매움";
        }
    }

    //타바스코 소스와 비교한 매운맛 계산 compareScovilleWithTabasco
    private String compareSpiciness(Integer scoville){
        if(scoville == null) {
            return "매운맛 정보 없음";
        } else if (scoville > TABASCO_SCOVILLE*1.5) {
            return"타바스코 소스 보다 완전 매운 편";
        } else if (scoville > TABASCO_SCOVILLE*0.8){
            return "타바스코 소스 보다 조금 더 매운 편";
        } else if (scoville < TABASCO_SCOVILLE*0.5){
            return "타바스코 소스 보다 안 매운 편";
        } else {
            return "타바스코 소스와 비슷한 편";
        }
    }

    //csv데이터를 읽고 db에 저장
    public void saveFoodsFromCsv(MultipartFile file){
        if(file.isEmpty()){
            throw new RuntimeException("CSV파일이 비어 있습니다.");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))){
            List<Food> foodList = br.lines()
                    .skip(1)  // 첫번째 줄 건너뛰기
                    .map(line -> {
                        String[] data = line.split(",");

                        if(data.length < 6 || data[1].trim().isEmpty()){
                            log.info("잘못된 데이터 건너뜀: " + Arrays.toString(data));
                            return null;
                        }

                        String name = data[1].trim();
                        Optional<Food> existingFood = foodRepository.findByName(name);
                        
                        if(existingFood.isPresent()){
                            log.info("이미 존재하는 음식 건너뜀: " + name);
                            return null;
                        }

                        //scoville 값이 유효한 숫자인지 검사
                        Integer scoville = null;
                        try {
                            if(!data[3].trim().isEmpty()){
                                scoville = Integer.parseInt(data[3].trim());
                            }
                        }catch (NumberFormatException e){
                            log.info("잘못된 scoville 값: " + data[3].trim());
                        }

                        //Food 객체 생성
                        Food food = Food.builder()
                                .category(data[0].trim())
                                .name(data[1].trim())
                                .englishName(data[2].trim())
                                .scoville(scoville)
                                .imgUrl(data[4].trim())
                                .description(data[5].trim())
                                .build();

                        log.info("저장할 데이터: " + food); // 디버깅 로그 추가
                        return food;

                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            //데이터 저장
            if(!foodList.isEmpty()) {
                foodRepository.saveAll(foodList);
                log.info("총 저장된 데이터 개수: " + foodList.size()); // 몇 개 저장됐는지 확인
                }else {
                log.info("저장할 데이터가 없습니다.");
                }
            } catch (Exception e){
                throw new RuntimeException("CSV 파일을 읽는 중 오류 발생", e);
        }
    }

    //카테고리별 음식 리스트 조회, 정렬 기능(최신, 인기순)
    public List<Food> getFoods(String category, String sort){
        List<Food> foods = (category != null) ? foodRepository.findByCategory(category) : foodRepository.findAll();

        foods = foodRepository.findAll(); // 최신 데이터 반영

        // 정렬 기준 적용(기본값: 인기순)
        if (sort == null || "popular".equalsIgnoreCase(sort)){
            //좋아요 개수를 기준으로 내림차순 정렬
            foods.sort(Comparator.comparingInt(Food::getHeartCount).reversed());
        } else {
            // 최신순 정렬 (ID 기준 내림차순)
            foods.sort(Comparator.comparing(Food::getId).reversed());
        }

        return foods;
    }

    //음식 검색(이름 또는 카테고리)
    public List<Food> searchFoods(String query){
        return foodRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query);
    }

}
