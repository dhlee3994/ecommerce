package io.hhplus.ecommerce.product.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

	@DisplayName("상품 객체를 생성할 수 있다.")
	@Test
	void createProduct() throws Exception {
		// given
		final String name = "상품명1";
		final int price = 1000;
		final int quantity = 10;

		// when
		final Product result = Product.builder()
			.name(name)
			.price(price)
			.quantity(quantity)
			.build();

		// then
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getPrice()).isEqualTo(price);
		assertThat(result.getQuantity()).isEqualTo(quantity);
	}

	@DisplayName("상품 재고를 변경할 수 있다.")
	@Test
	void updateQuantity() throws Exception {
		// given
		final int quantity = 10;
		final Product product = Product.builder()
			.quantity(quantity)
			.build();

		final int updateQuantity = 5;

		// when
		product.updateQuantity(updateQuantity);

		// then
		assertThat(product.getQuantity()).isEqualTo(updateQuantity);
	}
}
