package io.hhplus.ecommerce.product.domain;

import static io.hhplus.ecommerce.global.exception.ErrorCode.STOCK_QUANTITY_NOT_ENOUGH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.hhplus.ecommerce.global.exception.EcommerceException;

class StockServiceTest {

	private StockService stockService;

	@BeforeEach
	void setUp() {
		stockService = new StockService();
	}

	@DisplayName("주문으로 인한 상품 재고 차감에 성공하면 주문으로 인한 재고 변경 이력을 반환한다.")
	@Test
	void decrease() throws Exception {
		// given
		final int beforeQuantity = 10;
		final int decreaseQuantity = 4;
		final int expectedQuantity = 6;

		final Product product = Product.builder().build();
		final Stock stock = Stock.builder().productId(product.getId()).quantity(beforeQuantity).build();

		// when
		stockService.decrease(product, stock, decreaseQuantity);

		// then
		assertThat(stock.getQuantity()).isEqualTo(expectedQuantity);
		assertThat(product.getQuantity()).isEqualTo(stock.getQuantity());
	}

	@DisplayName("상품 재고를 초과해서 주문 재고차감요청을 하면 EcommerceException 예외가 발생한다.")
	@Test
	void issueOverLimit() throws Exception {
		// given
		final int beforeQuantity = 10;
		final int decreaseQuantity = 11;

		final Product product = Product.builder().build();
		final Stock stock = Stock.builder().productId(product.getId()).quantity(beforeQuantity).build();

		// when & then
		assertThatThrownBy(() -> stockService.decrease(product, stock, decreaseQuantity))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(STOCK_QUANTITY_NOT_ENOUGH.getMessage());
	}
}
