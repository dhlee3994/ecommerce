package io.hhplus.ecommerce.product.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "상품 단건 조회, 목록 응답")
@Getter
public class ProductApiResponse {

	@Schema(description = "상품 ID", example = "1")
	private final long productId;

	@Schema(description = "상품명", example = "상품1")
	private final String name;

	@Schema(description = "상품 가격", example = "10000")
	private final int price;

	@Schema(description = "상품 수량", example = "100")
	private final int quantity;

	@Schema(description = "상품 상태", example = "판매중")
	private final String status;

	@Builder
	private ProductApiResponse(
		final long productId, final String name, final int price, final int quantity, final String status) {
		this.productId = productId;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.status = status;
	}
}
