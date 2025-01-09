package io.hhplus.ecommerce.payment.domain;

import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_ALREADY_USED;
import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_IS_EXPIRED;
import static io.hhplus.ecommerce.global.exception.ErrorCode.ORDER_ALREADY_PAID;
import static io.hhplus.ecommerce.global.exception.ErrorCode.POINT_IS_NOT_ENOUGH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.order.domain.OrderStatus;
import io.hhplus.ecommerce.point.domain.Point;

class PaymentServiceTest {

	private PaymentService paymentService;

	@BeforeEach
	void setUp() {
		paymentService = new PaymentService();
	}

	@DisplayName("주문, 보유 포인트, 쿠폰으로 결제할 수 있다.")
	@Test
	void pay() throws Exception {
		// given
		final int amount = 1000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();

		final int pointHeld = 10000;
		final Point point = Point.builder()
			.point(pointHeld)
			.build();

		final int discountAmount = 100;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountAmount(discountAmount)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();

		final int paymentAmount = amount - discountAmount;
		final int expectedPointHeld = pointHeld - paymentAmount;

		// when
		final Payment result = paymentService.pay(order, point, issuedCoupon);

		// then
		assertThat(result.getAmount()).isEqualTo(paymentAmount);
		assertThat(point.getPoint()).isEqualTo(expectedPointHeld);
		assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
		assertThat(issuedCoupon.getUsedAt()).isNotNull();
	}

	@DisplayName("만료된 쿠폰으로 결제를 시도하면 EcommerceException 예외가 발생한다.")
	@Test
	void payWithExpiredCoupon() throws Exception {
		// given
		final int amount = 1000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();

		final int pointHeld = 10000;
		final Point point = Point.builder()
			.point(pointHeld)
			.build();

		final int discountAmount = 100;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountAmount(discountAmount)
			.expiredAt(LocalDateTime.now().minusMinutes(1))
			.usedAt(null)
			.build();

		// when & then
		assertThatThrownBy(() -> paymentService.pay(order, point, issuedCoupon))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(COUPON_IS_EXPIRED.getMessage());
	}

	@DisplayName("이미 사용된 쿠폰으로 결제를 시도하면 EcommerceException 예외가 발생한다.")
	@Test
	void payWithUsedCoupon() throws Exception {
		// given
		final int amount = 1000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();

		final int pointHeld = 10000;
		final Point point = Point.builder()
			.point(pointHeld)
			.build();

		final int discountAmount = 100;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountAmount(discountAmount)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(LocalDateTime.now().minusMinutes(1))
			.build();

		// when & then
		assertThatThrownBy(() -> paymentService.pay(order, point, issuedCoupon))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(COUPON_ALREADY_USED.getMessage());
	}

	@DisplayName("결제 금액이 보유 포인트보다 많으면 EcommerceException 예외가 발생한다.")
	@Test
	void payWithNotEnoughPoint() throws Exception {
		// given
		final int amount = 1000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();

		final int pointHeld = 0;
		final Point point = Point.builder()
			.point(pointHeld)
			.build();

		final int discountAmount = 100;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountAmount(discountAmount)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();

		// when & then
		assertThatThrownBy(() -> paymentService.pay(order, point, issuedCoupon))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(POINT_IS_NOT_ENOUGH.getMessage());
	}

	@DisplayName("이미 결제한 주문을 결제시도하면 EcommerceException 예외가 발생한다.")
	@Test
	void payWithAlreadyOrder() throws Exception {
		// given
		final int amount = 1000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.PAID)
			.build();

		final int pointHeld = 10000;
		final Point point = Point.builder()
			.point(pointHeld)
			.build();

		final int discountAmount = 100;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountAmount(discountAmount)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();

		// when & then
		assertThatThrownBy(() -> paymentService.pay(order, point, issuedCoupon))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(ORDER_ALREADY_PAID.getMessage());
	}
}
