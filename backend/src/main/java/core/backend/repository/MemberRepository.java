package core.backend.repository;

import core.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// Member엔티티의 DB접근을 위한 Repo
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email); // 이메일로 회원 조회하는 메서드

    @Query(value = "WITH ReviewCounts AS (SELECT m.member_id, COUNT(r.review_id) AS review_count FROM members m LEFT JOIN review r ON m.member_id = r.member_id GROUP BY m.member_id), " +
            "UserRank AS (SELECT member_id, review_count, DENSE_RANK() OVER (ORDER BY review_count DESC) AS rank_num, COUNT(*) OVER () AS total_members FROM ReviewCounts) " +
            "SELECT ROUND((rank_num * 100.0) / total_members) AS percentile FROM UserRank WHERE member_id = :userId", nativeQuery = true)
    Integer getUserReviewPercentile(@Param("userId") Long userId);
}
