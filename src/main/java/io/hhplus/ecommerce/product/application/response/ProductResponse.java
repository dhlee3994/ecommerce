package io.hhplus.ecommerce.product.application.response;

import io.hhplus.ecommerce.product.domain.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProductResponse {

	private final long id;
	private final String name;
	private final int price;
	private final int quantity;

	public static ProductResponse from(final Product product) {
		return ProductResponse.builder()
			.id(product.getId())
			.name(product.getName())
			.price(product.getPrice())
			.quantity(product.getQuantity())
			.build();
	}
}
