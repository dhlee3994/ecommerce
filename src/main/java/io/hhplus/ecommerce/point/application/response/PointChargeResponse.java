package io.hhplus.ecommerce.point.application.response;

import io.hhplus.ecommerce.point.domain.Point;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointChargeResponse {

	private final int point;

	public static PointChargeResponse from(final Point point) {
		return PointChargeResponse.builder()
			.point(point.getPoint())
			.build();
	}
}
