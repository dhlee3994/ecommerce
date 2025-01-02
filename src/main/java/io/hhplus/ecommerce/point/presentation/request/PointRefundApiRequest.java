package io.hhplus.ecommerce.point.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "포인트 환불 요청")
@Getter
public class PointRefundApiRequest {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Schema(description = "환불 포인트 금액", example = "1000")
	private final int amount;

	@Builder
	private PointRefundApiRequest(final Long userId, final int amount) {
		this.userId = userId;
		this.amount = amount;
	}
}
