package io.hhplus.ecommerce.product.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.global.exception.ErrorCode;

class StockTest {

	@DisplayName("상품 재고 객체를 생성할 수 있다.")
	@Test
	void createStock() throws Exception {
		// given
		final Long productId = 1L;
		final int quantity = 10;

		// when
		final Stock result = Stock.builder()
			.productId(productId)
			.quantity(quantity)
			.build();

		// then
		assertThat(result.getProductId()).isEqualTo(productId);
		assertThat(result.getQuantity()).isEqualTo(quantity);
	}

	@DisplayName("상품 재고를 차감할 수 있다.")
	@Test
	void decreaseQuantity() throws Exception {
		// given
		final Long productId = 1L;
		final int quantity = 10;
		final Stock stock = Stock.builder()
			.productId(productId)
			.quantity(quantity)
			.build();

		final int deceaseQuantity = 4;
		final int expectedQuantity = 6;

		// when
		stock.decrease(deceaseQuantity);

		// then
		assertThat(stock.getQuantity()).isEqualTo(expectedQuantity);
	}

	@DisplayName("보유 재고보다 많은 양을 차감하려고 하면 EcommerceException 예외가 발생한다.")
	@Test
	void decreaseQuantityOverLimit() throws Exception {
		final Long productId = 1L;
		final int quantity = 0;
		final Stock stock = Stock.builder()
			.productId(productId)
			.quantity(quantity)
			.build();

		final int deceaseQuantity = 1;

		assertThatThrownBy(() -> stock.decrease(deceaseQuantity))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(ErrorCode.STOCK_QUANTITY_NOT_ENOUGH.getMessage());
	}
}
