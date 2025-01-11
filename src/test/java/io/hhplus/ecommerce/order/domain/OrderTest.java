package io.hhplus.ecommerce.order.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

	@DisplayName("주문 객체를 생성할 수 있다.")
	@Test
	void createOrder() throws Exception {
		// given
		final Long userId = 1L;
		final int amount = 1000;
		final OrderStatus status = OrderStatus.ORDERED;

		// when
		final Order result = Order.builder()
			.userId(userId)
			.amount(amount)
			.status(status)
			.build();

		// then
		assertThat(result).isNotNull()
			.extracting("userId", "amount", "status")
			.containsExactly(userId, amount, status);
	}

	@DisplayName("유저아이디로 기본 주문 객체를 생성할 수 있다.")
	@Test
	void createDefaultOrder() throws Exception {
		// given
		final Long userId = 1L;

		// when
		final Order result = Order.createOrder(userId);

		// then
		assertThat(result).isNotNull()
			.extracting("userId", "amount", "status")
			.containsExactly(userId, 0, OrderStatus.ORDERED);
	}

	@DisplayName("주문 상품을 통해 주문 총 가격을 계산할 수 있다.")
	@Test
	void addOrderItem() throws Exception {
		// given
		final Long userId = 1L;
		final Order order = Order.createOrder(userId);

		assertThat(order.getAmount()).isZero();

		final int price = 1000;
		final int quantity = 10;
		final OrderItem orderitem = OrderItem.builder()
			.price(price)
			.quantity(quantity)
			.build();

		final int amount = price * quantity;

		// when
		order.addOrderItem(orderitem);

		// then
		assertThat(order.getAmount()).isEqualTo(amount);
	}

	@DisplayName("할인 금액을 받아서 결제해야할 가격을 계산할 수 있다.")
	@Test
	void calculatePaymentPrice() throws Exception {
		// given
		final Long userId = 1L;
		final int amount = 10000;
		final Order order = Order.builder()
			.userId(userId)
			.amount(amount)
			.build();

		final int discountAmount = 1000;
		final int expectedAmount = amount - discountAmount;

		// when
		final int result = order.calculatePaymentPrice(discountAmount);

		// then
		assertThat(result).isEqualTo(expectedAmount);
	}
}
