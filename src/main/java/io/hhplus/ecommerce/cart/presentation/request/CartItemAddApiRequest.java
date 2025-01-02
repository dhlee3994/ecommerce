package io.hhplus.ecommerce.cart.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "장바구니 상품 추가 요청")
@Getter
public class CartItemAddApiRequest {

	@Schema(description = "상품 ID", example = "1")
	private final Long productId;

	@Schema(description = "장바구니에 담을 상품 수량", example = "100")
	private final int quantity;

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Builder
	private CartItemAddApiRequest(final Long productId, final int quantity, final Long userId) {
		this.productId = productId;
		this.quantity = quantity;
		this.userId = userId;
	}
}
