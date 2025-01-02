package io.hhplus.ecommerce.order.presentation.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 생성 요청")
@Getter
public class OrderCreateApiRequest {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Schema(description = "주문 상품 목록")
	private final List<OrderItemCreateApiRequest> orderItems;

	@Schema(description = "주문에 적용할 쿠폰 ID", example = "1")
	private final Long orderCouponId;

	@Builder
	private OrderCreateApiRequest(
		final Long userId, final List<OrderItemCreateApiRequest> orderItems, final Long orderCouponId) {
		this.userId = userId;
		this.orderItems = orderItems;
		this.orderCouponId = orderCouponId;
	}
}
