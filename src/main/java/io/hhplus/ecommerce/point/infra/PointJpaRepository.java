package io.hhplus.ecommerce.point.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.point.domain.Point;

public interface PointJpaRepository extends JpaRepository<Point, Long> {

	Optional<Point> findByUserId(long userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Point p where p.userId = :userId")
	Optional<Point> findByUserIdForUpdate(long userId);
}
