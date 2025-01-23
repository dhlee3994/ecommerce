package io.hhplus.ecommerce.point.domain;

import static io.hhplus.ecommerce.global.exception.ErrorCode.CHARGE_POINT_SHOULD_BE_POSITIVE;
import static io.hhplus.ecommerce.global.exception.ErrorCode.POINT_IS_NOT_ENOUGH;
import static io.hhplus.ecommerce.global.exception.ErrorCode.POINT_SHOULD_BE_POSITIVE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point")
@Entity
public class Point {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private int point;

	@Builder
	private Point(final Long userId, final int point) {
		if (point < 0) {
			throw new InvalidRequestException(POINT_SHOULD_BE_POSITIVE);
		}
		this.userId = userId;
		this.point = point;
	}

	public static Point empty(final long userId) {
		return Point.builder()
			.userId(userId)
			.point(0)
			.build();
	}

	public void charge(final int chargePoint) {
		if (chargePoint <= 0) {
			throw new EcommerceException(CHARGE_POINT_SHOULD_BE_POSITIVE);
		}
		this.point += chargePoint;
	}

	public void use(final int amount) {
		if (this.point < amount) {
			throw new EcommerceException(POINT_IS_NOT_ENOUGH);
		}
		this.point -= amount;
	}
}
