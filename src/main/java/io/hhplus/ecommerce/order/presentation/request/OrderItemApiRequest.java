package io.hhplus.ecommerce.order.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 상품 목록 조회 요청")
@Getter
public class OrderItemApiRequest {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Schema(description = "주문 ID", example = "1")
	private final Long orderId;

	@Builder
	private OrderItemApiRequest(final Long userId, final Long orderId) {
		this.userId = userId;
		this.orderId = orderId;
	}
}
