package core.backend.repository;

import core.backend.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    //특정 이름 음식 조회
    Optional<Food> findByName(String name);

    //음식 이름, 카테고리로 검색(부분 일치)
    List<Food> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category);

    //특정 카테고리의 음식 목록 조회
    List<Food> findByCategory(String category);

    //fetch join을 이용해 음식과 연관된 리뷰와 좋아요를 한 번에 조회
    @Query("select f from Food f left join fetch f.reviews left join fetch f.hearts where f.id = :foodId")
    Optional<Food> findByIdWithReviewsAndHearts(@Param("foodId") Long foodId);

    @Query("SELECT f FROM Food f LEFT JOIN f.hearts h GROUP BY f ORDER BY COUNT(h) DESC LIMIT 2")
    List<Food> findTop2MostLikedFoods();
}
