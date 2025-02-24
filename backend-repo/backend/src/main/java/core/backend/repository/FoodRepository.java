package core.backend.repository;

import core.backend.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
