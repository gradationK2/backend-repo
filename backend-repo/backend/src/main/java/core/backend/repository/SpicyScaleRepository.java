package core.backend.repository;

import core.backend.domain.SpicyScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpicyScaleRepository extends JpaRepository<SpicyScale, Long> {
}
