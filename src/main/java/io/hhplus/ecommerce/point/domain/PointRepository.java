package io.hhplus.ecommerce.point.domain;

import java.util.Optional;

public interface PointRepository {

	Point save(Point point);

	Optional<Point> findByUserId(long userId);

	Optional<Point> findByUserIdForUpdate(long userId);
}
