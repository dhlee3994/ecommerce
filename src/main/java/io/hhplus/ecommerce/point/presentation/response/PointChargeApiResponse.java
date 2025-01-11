package io.hhplus.ecommerce.point.presentation.response;

import io.hhplus.ecommerce.point.application.response.PointChargeResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "포인트 충전 응답")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointChargeApiResponse {

	@Schema(description = "충전 후 포인트", example = "10000")
	private final int point;

	public static PointChargeApiResponse from(final PointChargeResponse chargeResponse) {
		return PointChargeApiResponse.builder()
			.point(chargeResponse.getPoint())
			.build();
	}
}
