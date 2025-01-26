package io.hhplus.ecommerce.point.application;

import static io.hhplus.ecommerce.global.exception.ErrorCode.POINT_NOT_FOUND;
import static io.hhplus.ecommerce.global.exception.ErrorCode.USER_NOT_FOUND;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.ecommerce.point.application.request.PointChargeRequest;
import io.hhplus.ecommerce.point.application.response.PointChargeResponse;
import io.hhplus.ecommerce.point.application.response.PointResponse;
import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.point.domain.PointRepository;
import io.hhplus.ecommerce.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PointApplicationService {

	private final PointRepository pointRepository;
	private final UserRepository userRepository;

	public PointResponse getPoint(final Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new EntityNotFoundException(USER_NOT_FOUND.getMessage());
		}

		final Point point = pointRepository.findByUserId(userId)
			.orElseGet(() -> pointRepository.save(Point.empty(userId)));
		return PointResponse.from(point);
	}

	@Transactional
	public PointChargeResponse charge(final PointChargeRequest request) {
		if (!userRepository.existsById(request.getUserId())) {
			throw new EntityNotFoundException(USER_NOT_FOUND.getMessage());
		}

		final Point point = pointRepository.findByUserIdForUpdate(request.getUserId())
			.orElseThrow(() -> new EntityNotFoundException(POINT_NOT_FOUND.getMessage()));

		point.charge(request.getChargePoint());
		return PointChargeResponse.from(point);
	}
}
