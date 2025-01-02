package io.hhplus.ecommerce.cart.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "장바구니 상품 추가 응답")
@Getter
public class CartItemAddApiResponse {

	@Schema(description = "장바구니 상품 ID", example = "1")
	private final Long cartItemId;

	@Schema(description = "상품 ID", example = "1")
	private final Long productId;

	@Schema(description = "상품명", example = "상품1")
	private final String name;

	@Schema(description = "상품 가격", example = "10000")
	private final int price;

	@Schema(description = "장바구니에 담긴 상품 수량", example = "100")
	private final int quantity;

	@Builder
	private CartItemAddApiResponse(
		final Long cartItemId, final Long productId, final String name, final int price, final int quantity) {
		this.cartItemId = cartItemId;
		this.productId = productId;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}
}
