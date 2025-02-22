package core.backend.repository;

import core.backend.domain.Member;
import core.backend.domain.Review;
import core.backend.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewAndMember(Review review, Member member);

    void deleteAllByReviewIn(List<Review> reviews);
}
