package io.hhplus.ecommerce.point.presentation.request;

import io.hhplus.ecommerce.point.application.request.PointChargeRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "포인트 충전 요청")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointChargeApiRequest {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Schema(description = "충전 포인트 금액", example = "1000")
	private final int chargePoint;

	public PointChargeRequest toServiceRequest() {
		return PointChargeRequest.builder()
			.userId(userId)
			.chargePoint(chargePoint)
			.build();
	}
}
