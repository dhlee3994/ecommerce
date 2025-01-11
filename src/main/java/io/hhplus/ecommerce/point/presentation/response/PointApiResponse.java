package io.hhplus.ecommerce.point.presentation.response;

import io.hhplus.ecommerce.point.application.response.PointResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "포인트 조회 응답")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointApiResponse {

	@Schema(description = "사용자 ID", example = "1")
	private final long userId;

	@Schema(description = "보유 포인트 잔액", example = "10000")
	private final int point;

	public static PointApiResponse from(final PointResponse pointResponse) {
		return PointApiResponse.builder()
			.userId(pointResponse.getUserId())
			.point(pointResponse.getPoint())
			.build();
	}
}
