package io.hhplus.ecommerce.order.presentation.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 목록 응답")
@Getter
public class OrderApiResponse {

	@Schema(description = "주문 ID", example = "1")
	private final Long orderId;

	@Schema(description = "주문 총 금액", example = "10000")
	private final int amount;

	@Schema(description = "적용한 쿠폰 아이디", example = "1")
	private final Long couponId;

	@Schema(description = "적용한 쿠폰명", example = "쿠폰1")
	private final String couponName;

	@Schema(description = "주문에 적용된 할인 금액", example = "0")
	private final int discountAmount;

	@Schema(description = "할인이 적용된 주문 금액", example = "10000")
	private final int totalAmount;

	@Schema(description = "주문 상태", example = "ORDERED")
	private final String status;

	@Schema(description = "주문 생성 일시", example = "2025-01-02 12:00:00")
	private final LocalDateTime orderedAt;

	@Builder
	private OrderApiResponse(
		final Long orderId, final int amount, final Long couponId, final String couponName,
		final int discountAmount, final int totalAmount, final String status,
		final LocalDateTime orderedAt
	) {
		this.orderId = orderId;
		this.amount = amount;
		this.couponId = couponId;
		this.couponName = couponName;
		this.discountAmount = discountAmount;
		this.totalAmount = totalAmount;
		this.status = status;
		this.orderedAt = orderedAt;
	}
}
