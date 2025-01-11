package io.hhplus.ecommerce.point.domain;

import static io.hhplus.ecommerce.global.exception.ErrorCode.CHARGE_POINT_SHOULD_BE_POSITIVE;
import static io.hhplus.ecommerce.global.exception.ErrorCode.POINT_SHOULD_BE_POSITIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;

public class PointTest {

	@DisplayName("포인트 객체 생성")
	@Nested
	class createPointA {

		@DisplayName("유저 아이디와 보유 포인트로 포인트 객체를 생성할 수 있다.")
		@Test
		void createPoint() {
			// given
			final Long userId = 1L;
			final int point = 1000;

			// when
			final Point result = Point.builder()
				.userId(userId)
				.point(point)
				.build();

			// then
			assertThat(result).isNotNull()
				.extracting("userId", "point")
				.containsExactly(userId, point);
		}

		@DisplayName("보유 포인트가 음수인 포인트 객체를 생성하려고 하면 InvalidRequestException 예외가 발생한다.")
		@Test
		void createNotPositivePointThrowsException() {
			// given
			final Long userId = 1L;
			final int point = -1;

			// when & then
			assertThatThrownBy(() -> Point.builder()
					.userId(userId)
					.point(point)
					.build())
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(POINT_SHOULD_BE_POSITIVE.getMessage());
		}

		@DisplayName("유저 아이디로 보유 포인트가 0인 포인트 객체를 생성할 수 있다.")
		@Test
		void createEmptyPoint() {
			// given
			final Long userId = 1L;

			// when
			final Point result = Point.empty(userId);

			// then
			assertThat(result).isNotNull()
				.extracting("userId", "point")
				.containsExactly(userId, 0);
		}
	}

	@DisplayName("포인트 충전")
	@Nested
	class chargePoint {

		@DisplayName("포인트 충전에 성공하면 보유 포인트와 충전 요청 포인트가 합산된 포인트가 된다.")
		@Test
		void createEmptyPoint() {
			// given
			final Long userId = 1L;
			final int beforePoint = 1000;
			final int amount = 1000;

			final Point point = Point.builder()
				.userId(userId)
				.point(beforePoint)
				.build();

			// when
			point.charge(amount);

			// then
			assertThat(point).isNotNull()
				.extracting("userId", "point")
				.containsExactly(userId, 2000);
		}

		@DisplayName("포인트 충전시 충전 요청 포인트가 0이면 EcommerceException 예외가 발생한다.")
		@Test
		void chargeWithZeroAmount() {
			// given
			final int chargePoint = 0;
			final Point point = Point.empty(1L);

			// when & then
			assertThatThrownBy(() -> point.charge(chargePoint))
				.isInstanceOf(EcommerceException.class)
				.hasMessage(CHARGE_POINT_SHOULD_BE_POSITIVE.getMessage());
		}

		@DisplayName("포인트 충전시 충전 요청 포인트가 음수이면 EcommerceException 예외가 발생한다.")
		@Test
		void chargeWithNegativeAmount() {
			// given
			final int chargePoint = -10;
			final Point point = Point.empty(1L);

			// when & then
			assertThatThrownBy(() -> point.charge(chargePoint))
				.isInstanceOf(EcommerceException.class)
				.hasMessage(CHARGE_POINT_SHOULD_BE_POSITIVE.getMessage());
		}
	}
}
