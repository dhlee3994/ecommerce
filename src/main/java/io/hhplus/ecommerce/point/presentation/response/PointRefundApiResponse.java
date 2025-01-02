package io.hhplus.ecommerce.point.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "포인트 환불 응답")
@Getter
public class PointRefundApiResponse {

	@Schema(description = "환불 요청한 포인트", example = "10000")
	private final int amount;

	@Schema(description = "환불하기전 보유 포인트", example = "10000")
	private final int beforePoint;

	@Schema(description = "환불 후 포인트", example = "0")
	private final int afterPoint;

	@Builder
	private PointRefundApiResponse(final int amount, final int beforePoint, final int afterPoint) {
		this.amount = amount;
		this.beforePoint = beforePoint;
		this.afterPoint = afterPoint;
	}
}
