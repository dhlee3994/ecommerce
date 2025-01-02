package io.hhplus.ecommerce.order.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문에 생성될 상품 정보")
@Getter
public class OrderItemCreateApiRequest {

	@Schema(description = "상품 ID", example = "1")
	private final Long productId;

	@Schema(description = "주문 상품 수량", example = "100")
	private final int quantity;

	@Schema(description = "상품에 적용할 쿠폰 ID", example = "2")
	private final Long couponId;

	@Builder
	private OrderItemCreateApiRequest(final Long productId, final int quantity, final Long couponId) {
		this.productId = productId;
		this.quantity = quantity;
		this.couponId = couponId;
	}
}
