package io.hhplus.ecommerce.point.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "포인트 충전 응답")
@Getter
public class PointChargeApiResponse {

	@Schema(description = "충전 요청한 포인트", example = "10000")
	private final int amount;

	@Schema(description = "충전하기전 보유 포인트", example = "0")
	private final int beforePoint;

	@Schema(description = "충전 후 포인트", example = "10000")
	private final int afterPoint;

	@Builder
	private PointChargeApiResponse(final int amount, final int beforePoint, final int afterPoint) {
		this.amount = amount;
		this.beforePoint = beforePoint;
		this.afterPoint = afterPoint;
	}
}
