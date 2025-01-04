package io.hhplus.ecommerce.order.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 상품 응답")
@Getter
public class OrderItemApiResponse {

	@Schema(description = "주문 상품 ID", example = "1")
	private final Long orderItemId;

	@Schema(description = "상품 ID", example = "2")
	private final Long productId;

	@Schema(description = "주문 상품 이름", example = "상품 이름")
	private final String name;

	@Schema(description = "주문 상품 가격", example = "10000")
	private final int price;

	@Schema(description = "주문 상품 수량", example = "1")
	private final int quantity;

	@Schema(description = "주문 상품 금액", example = "10000")
	private final int amount;

	@Schema(description = "적용한 쿠폰 아이디", example = "1")
	private final Long couponId;

	@Schema(description = "적용한 쿠폰명", example = "쿠폰1")
	private final String couponName;

	@Schema(description = "상품에 적용된 할인 금액", example = "1000")
	private final int discountAmount;

	@Schema(description = "할인이 적용된 상품 금액", example = "9000")
	private final int totalAmount;

	@Builder
	private OrderItemApiResponse(
		final Long orderItemId,
		final Long productId,
		final String name, final int price, final int quantity,
		final int amount, final Long couponId, final String couponName,
		final int discountAmount, final int totalAmount
	) {
		this.orderItemId = orderItemId;
		this.productId = productId;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.amount = amount;
		this.couponId = couponId;
		this.couponName = couponName;
		this.discountAmount = discountAmount;
		this.totalAmount = totalAmount;
	}
}
