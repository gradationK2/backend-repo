package core.backend.repository;

import java.util.List;

import core.backend.domain.Food;
import core.backend.domain.Member;
import core.backend.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByMemberId(Long userId);
    List<Review> findByFood(Food food); //특정 음식에 대한 리뷰 조회

    List<Review> findByFoodId(Long foodID);

    void deleteAllByMember(Member member);
}
