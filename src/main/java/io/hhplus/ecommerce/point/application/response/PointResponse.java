package io.hhplus.ecommerce.point.application.response;

import io.hhplus.ecommerce.point.domain.Point;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointResponse {

	private final long userId;
	private final int point;

	public static PointResponse from(final Point point) {
		return PointResponse.builder()
			.userId(point.getUserId())
			.point(point.getPoint())
			.build();
	}
}
