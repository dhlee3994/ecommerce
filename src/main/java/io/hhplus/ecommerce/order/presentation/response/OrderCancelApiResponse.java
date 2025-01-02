package io.hhplus.ecommerce.order.presentation.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 취소 응답")
@Getter
public class OrderCancelApiResponse {

	@Schema(description = "주문 ID", example = "1")
	private final Long orderId;

	@Schema(description = "환불받을 포인트", example = "1000")
	private final int refundPoint;

	@Schema(description = "주문 취소 날짜", example = "2025-01-01 10:00:00")
	private final LocalDateTime canceledAt;

	@Builder
	private OrderCancelApiResponse(final Long orderId, final int refundPoint, final LocalDateTime canceledAt) {
		this.orderId = orderId;
		this.refundPoint = refundPoint;
		this.canceledAt = canceledAt;
	}
}
