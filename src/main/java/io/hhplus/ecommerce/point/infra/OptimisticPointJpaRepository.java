package io.hhplus.ecommerce.point.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.point.domain.Point;

@Profile("optimistic-lock")
public interface OptimisticPointJpaRepository extends PointJpaRepository {

	@Lock(LockModeType.OPTIMISTIC)
	@Query("select p from Point p where p.userId = :userId")
	Optional<Point> findByUserIdForUpdate(long userId);
}
