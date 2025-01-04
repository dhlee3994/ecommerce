package io.hhplus.ecommerce.product.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "베스트 상품 응답")
@Getter
public class BestProductApiResponse {

	@Schema(description = "상품 ID", example = "1")
	private final Long productId;

	@Schema(description = "상품명", example = "상품명")
	private final String name;

	@Schema(description = "총 판매량", example = "100")
	private final int totalSales;

	@Schema(description = "총 판매 금액", example = "1000000")
	private final int totalAmount;

	@Builder
	private BestProductApiResponse(
		final Long productId, final String name, final int totalSales, final int totalAmount) {
		this.productId = productId;
		this.name = name;
		this.totalSales = totalSales;
		this.totalAmount = totalAmount;
	}
}
