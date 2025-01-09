package io.hhplus.ecommerce.order.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderItemTest {

	@DisplayName("주문 상품 객체를 생성할 수 있다.")
	@Test
	void createOrderItem() throws Exception {
		// given
		final Long orderId = 1L;
		final Long productId = 1L;
		final int quantity = 10;
		final int price = 1000;

		// when
		final OrderItem result = OrderItem.builder()
			.orderId(orderId)
			.productId(productId)
			.price(price)
			.quantity(quantity)
			.build();

		// then
		assertThat(result).isNotNull()
			.extracting("orderId", "productId", "price", "quantity")
			.containsExactly(orderId, productId, price, quantity);
	}

	@DisplayName("주문 상품의 가격을 계산할 수 있다.")
	@Test
	void calculatePrice() throws Exception {
		// given
		final int quantity = 10;
		final int price = 1000;

		final OrderItem orderItem = OrderItem.builder()
			.price(price)
			.quantity(quantity)
			.build();

		final int expectedPrice = price * quantity;

		// when
		final int result = orderItem.calculatePrice();

		// then
		assertThat(result).isEqualTo(expectedPrice);
	}
}
