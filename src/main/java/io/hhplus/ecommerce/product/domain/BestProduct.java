package io.hhplus.ecommerce.product.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BestProduct {

	private final Long productId;
	private final String name;
	private final long totalSaleCount;
}
