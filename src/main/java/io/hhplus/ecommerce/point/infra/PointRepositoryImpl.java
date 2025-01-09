package io.hhplus.ecommerce.point.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.point.domain.PointRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PointRepositoryImpl implements PointRepository {

	private final PointJpaRepository pointJpaRepository;

	@Override
	public Point save(final Point point) {
		return pointJpaRepository.save(point);
	}

	@Override
	public Optional<Point> findByUserId(final long userId) {
		return pointJpaRepository.findByUserId(userId);
	}

	@Override
	public Optional<Point> findByUserIdForUpdate(final long userId) {
		return pointJpaRepository.findByUserIdForUpdate(userId);
	}
}
