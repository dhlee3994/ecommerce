package io.hhplus.ecommerce.point.application;

import static io.hhplus.ecommerce.global.exception.ErrorCode.CHARGE_POINT_SHOULD_BE_POSITIVE;
import static io.hhplus.ecommerce.global.exception.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.point.application.request.PointChargeRequest;
import io.hhplus.ecommerce.point.application.response.PointChargeResponse;
import io.hhplus.ecommerce.point.application.response.PointResponse;
import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.point.domain.PointRepository;
import io.hhplus.ecommerce.user.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
class PointApplicationServiceUnitTest {

	@InjectMocks
	private PointApplicationService pointApplicationService;

	@Mock
	private PointRepository pointRepository;

	@Mock
	private UserRepository userRepository;

	@DisplayName("포인트 조회 기능")
	@Nested
	class GetPoint {

		@DisplayName("유저 아이디로 유저가 보유한 포인트를 조회할 수 있다.")
		@Test
		void getPointByUserId() throws Exception {
			// given
			final long userId = 1L;
			final int pointHeld = 10000;

			given(userRepository.existsById(userId))
				.willReturn(true);

			final Point point = Point.builder()
				.userId(userId)
				.point(pointHeld)
				.build();

			given(pointRepository.findByUserId(userId))
				.willReturn(Optional.of(point));

			// when
			final PointResponse result = pointApplicationService.getPoint(userId);

			// then
			assertThat(result).isNotNull()
				.extracting("userId", "point")
				.containsExactly(userId, pointHeld);
		}

		@DisplayName("유저 아이디로 보유한 포인트가 없으면 0포인트를 반환한다.")
		@Test
		void getPointByUserIdNoPoint() throws Exception {
			// given
			final long userId = 1L;
			final int emptyPoint = 0;

			given(userRepository.existsById(userId))
				.willReturn(true);

			given(pointRepository.findByUserId(userId))
				.willReturn(Optional.empty());

			final Point point = Point.builder()
				.userId(userId)
				.point(emptyPoint)
				.build();

			given(pointRepository.save(any(Point.class)))
				.willReturn(point);

			// when
			final PointResponse result = pointApplicationService.getPoint(userId);

			// then
			assertThat(result).isNotNull()
				.extracting("userId", "point")
				.containsExactly(userId, emptyPoint);

			then(pointRepository).should(times(1)).save(any(Point.class));
		}

		@DisplayName("존재하지 않는 유저의 포인트를 조회하면 EntityNotFoundException 예외가 발생한다.")
		@Test
		void getPointByInvalidUser() throws Exception {
			// given
			final long userId = 1L;

			given(userRepository.existsById(userId))
				.willReturn(false);

			// when & then
			assertThatThrownBy(() -> pointApplicationService.getPoint(userId))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(USER_NOT_FOUND.getMessage());
		}
	}

	@DisplayName("포인트 충전 기능")
	@Nested
	class chargePoint {

		@DisplayName("포인트를 충전할 수 있다.")
		@Test
		void chargePoint() throws Exception {
			// given
			final Long userId = 1L;
			given(userRepository.existsById(userId))
				.willReturn(true);
			final int pointHeld = 1000;
			final int chargePoint = 1000;

			final int expectedPoint = pointHeld + chargePoint;

			final PointChargeRequest request = PointChargeRequest.builder()
				.userId(userId)
				.chargePoint(chargePoint)
				.build();

			final Point point = Point.builder()
				.userId(userId)
				.point(pointHeld)
				.build();

			given(pointRepository.findByUserIdForUpdate(userId))
				.willReturn(Optional.of(point));

			// when
			final PointChargeResponse result = pointApplicationService.charge(request);

			// then
			assertThat(result.getPoint()).isEqualTo(expectedPoint);
		}

		@DisplayName("유효하지 않은 사용자의 포인트 충전을 요청하면 EntityNotFoundException 예외가 발생한다.")
		@Test
		void chargeInvalidUserPoint() throws Exception {
			// given
			final Long userId = 1L;
			given(userRepository.existsById(userId))
				.willReturn(false);

			final PointChargeRequest request = PointChargeRequest.builder()
				.userId(userId)
				.build();

			// when
			assertThatThrownBy(() -> pointApplicationService.charge(request))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(USER_NOT_FOUND.getMessage());
		}

		@DisplayName("충전 요청 포인트가 양수가 아니면 EcommerceException 예외가 발생한다.")
		@Test
		void chargeWithNotPositiveChargePoint() throws Exception {
			// given
			final Long userId = 1L;
			given(userRepository.existsById(userId))
				.willReturn(true);

			final int pointHeld = 1000;
			final Point point = Point.builder()
				.userId(userId)
				.point(pointHeld)
				.build();

			given(pointRepository.findByUserIdForUpdate(userId))
				.willReturn(Optional.of(point));

			final int chargePoint = 0;
			final PointChargeRequest request = PointChargeRequest.builder()
				.userId(userId)
				.chargePoint(chargePoint)
				.build();

			// when & then
			assertThatThrownBy(() -> pointApplicationService.charge(request))
				.isInstanceOf(EcommerceException.class)
				.hasMessage(CHARGE_POINT_SHOULD_BE_POSITIVE.getMessage())
			;
		}
	}
}
