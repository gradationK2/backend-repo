package core.backend.repository;

import java.util.List;
import core.backend.domain.Food;
import core.backend.domain.Heart;
import core.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
    boolean existsByFoodAndMember(Food food, Member member);

    void deleteByFoodAndMember(Food food, Member member);

    List<Heart> findAllByFood(Food food);

    List<Heart> findAllByMember(Member member);
}
