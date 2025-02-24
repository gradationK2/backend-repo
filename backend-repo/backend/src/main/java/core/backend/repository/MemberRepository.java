package core.backend.repository;

import core.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Member엔티티의 DB접근을 위한 Repo
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email); // 이메일로 회원 조회하는 메서드
}
