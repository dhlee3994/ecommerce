package io.hhplus.ecommerce.point.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "포인트 충전 요청")
@Getter
public class PointChargeApiRequest {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Schema(description = "충전 포인트 금액", example = "1000")
	private final int amount;

	@Builder
	private PointChargeApiRequest(final Long userId, final int amount) {
		this.userId = userId;
		this.amount = amount;
	}
}
