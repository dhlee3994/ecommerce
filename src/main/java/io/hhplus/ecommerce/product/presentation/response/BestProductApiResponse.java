package io.hhplus.ecommerce.product.presentation.response;

import io.hhplus.ecommerce.product.application.response.BestProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "베스트 상품 응답")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BestProductApiResponse {

	@Schema(description = "상품 ID", example = "1")
	private final Long productId;

	@Schema(description = "상품명", example = "상품명")
	private final String name;

	@Schema(description = "총 판매량", example = "100")
	private final long totalSaleCount;

	public static BestProductApiResponse from(final BestProductResponse bestProductResponse) {
		return BestProductApiResponse.builder()
			.productId(bestProductResponse.getProductId())
			.name(bestProductResponse.getName())
			.totalSaleCount(bestProductResponse.getTotalSaleCount())
			.build();
	}
}
