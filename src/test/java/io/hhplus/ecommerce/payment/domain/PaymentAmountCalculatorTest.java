package io.hhplus.ecommerce.payment.domain;

import static io.hhplus.ecommerce.global.exception.ErrorCode.DISCOUNT_AMOUNT_IS_LARGER_THAN_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.hhplus.ecommerce.coupon.domain.DiscountType;
import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.order.domain.OrderStatus;
import io.hhplus.ecommerce.payment.domain.discount.DiscountCalculator;
import io.hhplus.ecommerce.payment.domain.discount.DiscountPolicyFactory;
import io.hhplus.ecommerce.payment.domain.discount.FixedDiscountPolicy;
import io.hhplus.ecommerce.payment.domain.discount.NoneDiscountPolicy;
import io.hhplus.ecommerce.payment.domain.discount.RateDiscountPolicy;
import io.hhplus.ecommerce.util.EntityIdSetter;

class PaymentAmountCalculatorTest {

	private PaymentAmountCalculator paymentAmountCalculator;

	@BeforeEach
	void setUp() {
		final DiscountPolicyFactory discountPolicyFactory = new DiscountPolicyFactory(
			new NoneDiscountPolicy(),
			new FixedDiscountPolicy(),
			new RateDiscountPolicy()
		);
		final DiscountCalculator discountCalculator = new DiscountCalculator(discountPolicyFactory);

		paymentAmountCalculator = new PaymentAmountCalculator(discountCalculator);
	}

	@DisplayName("빈 쿠폰을 적용한 주문에 대한 결제 금액을 계산할 수 있다.")
	@Test
	void calculatePaymentAmountWithEmptyCoupon() throws Exception {
		// given
		final Long orderId = 1L;
		final int amount = 1000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();
		EntityIdSetter.setId(order, orderId);

		final IssuedCoupon issuedCoupon = IssuedCoupon.emptyCoupon();
		final int paymentAmount = amount;

		// when
		final int result = paymentAmountCalculator.calculatePaymentAmount(order, issuedCoupon);

		// then
		assertThat(result).isEqualTo(paymentAmount);
	}

	@DisplayName("정액할인 쿠폰을 적용한 주문에 대한 결제 금액을 계산할 수 있다.")
	@Test
	void calculatePaymentAmountWithFixedCoupon() throws Exception {
		// given
		final Long orderId = 1L;
		final int amount = 1000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();
		EntityIdSetter.setId(order, orderId);

		final DiscountType discountType = DiscountType.FIXED;
		final int discountValue = 1000;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountType(discountType)
			.discountValue(discountValue)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();

		final int expectedPaymentAmount = amount - discountValue;

		// when
		final int result = paymentAmountCalculator.calculatePaymentAmount(order, issuedCoupon);

		// then
		assertThat(result).isEqualTo(expectedPaymentAmount);
	}

	@DisplayName("정률할인 쿠폰을 적용한 주문에 대한 결제 금액을 계산할 수 있다.")
	@Test
	void calculatePaymentAmountWithRateCoupon() throws Exception {
		// given
		final Long orderId = 1L;
		final int amount = 10000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();
		EntityIdSetter.setId(order, orderId);

		final DiscountType discountType = DiscountType.RATE;
		final int discountValue = 15;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountType(discountType)
			.discountValue(discountValue)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();

		final int expectedPaymentAmount = 8500;

		// when
		final int result = paymentAmountCalculator.calculatePaymentAmount(order, issuedCoupon);

		// then
		assertThat(result).isEqualTo(expectedPaymentAmount);
	}

	@DisplayName("쿠폰할인 금액이 주문 금액보다 큰 경우 결제 금액 계산시 EcommerceException 예외가 발생한다.")
	@Test
	void calculatePaymentAmountWithInvalidAmount() throws Exception {
		// given
		final Long orderId = 1L;
		final int amount = 10000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();
		EntityIdSetter.setId(order, orderId);

		final DiscountType discountType = DiscountType.FIXED;
		final int discountValue = amount + 1;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountType(discountType)
			.discountValue(discountValue)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();

		final int expectedPaymentAmount = 8500;

		// when & then
		assertThatThrownBy(() -> paymentAmountCalculator.calculatePaymentAmount(order, issuedCoupon))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(DISCOUNT_AMOUNT_IS_LARGER_THAN_AMOUNT.getMessage());
	}
}
