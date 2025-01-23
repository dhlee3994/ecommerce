package io.hhplus.ecommerce.point.infra;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.point.domain.Point;

@Profile("!optimistic-lock & !pessimistic-lock")
public interface PointJpaRepository extends JpaRepository<Point, Long> {

	Optional<Point> findByUserId(long userId);

	@Query("select p from Point p where p.userId = :userId")
	Optional<Point> findByUserIdForUpdate(long userId);
}
