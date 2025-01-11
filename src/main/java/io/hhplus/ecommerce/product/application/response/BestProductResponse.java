package io.hhplus.ecommerce.product.application.response;

import io.hhplus.ecommerce.product.domain.BestProduct;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BestProductResponse {

	private final long productId;
	private final String name;
	private final long totalSaleCount;

	public static BestProductResponse from(final BestProduct bestProduct) {
		return BestProductResponse.builder()
			.productId(bestProduct.getProductId())
			.name(bestProduct.getName())
			.totalSaleCount(bestProduct.getTotalSaleCount())
			.build();
	}
}
