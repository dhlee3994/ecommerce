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
import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.util.EntityIdSetter;

class PaymentServiceTest {

	private PaymentService paymentService;

	@BeforeEach
	void setUp() {
		final DiscountPolicyFactory discountPolicyFactory = new DiscountPolicyFactory(
			new NoneDiscountPolicy(),
			new FixedDiscountPolicy(),
			new RateDiscountPolicy()
		);
		final DiscountCalculator discountCalculator = new DiscountCalculator(discountPolicyFactory);

		paymentService = new PaymentService(discountCalculator);
	}

	@DisplayName("쿠폰을 적용하지 않으면 주문 총 금액을 결제한다.")
	@Test
	void payWithNoCoupon() throws Exception {
		// given
		final Long orderId = 1L;
		final int amount = 1000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();
		EntityIdSetter.setId(order, orderId);

		final int pointHeld = 10000;
		final Point point = Point.builder()
			.point(pointHeld)
			.build();

		final IssuedCoupon issuedCoupon = IssuedCoupon.emptyCoupon();

		final int paymentAmount = amount;
		final int expectedPointHeld = pointHeld - paymentAmount;

		// when
		final Payment result = paymentService.pay(order, point, issuedCoupon);

		// then
		assertThat(result.getAmount()).isEqualTo(paymentAmount);
		assertThat(point.getPoint()).isEqualTo(expectedPointHeld);
		assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

		assertThat(issuedCoupon.getOrderId()).isEqualTo(orderId);
		assertThat(issuedCoupon.getUsedAt()).isNotNull();
	}

	@DisplayName("정액할인 쿠폰으로 결제하면 쿠폰에 명시된 할인양만큼 차감된 금액으로 결제한다.")
	@Test
	void payWithFixedCoupon() throws Exception {
		// given
		final Long orderId = 1L;
		final int amount = 1000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();
		EntityIdSetter.setId(order, orderId);

		final int pointHeld = 10000;
		final Point point = Point.builder()
			.point(pointHeld)
			.build();

		final DiscountType discountType = DiscountType.FIXED;
		final int discountValue = 1000;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountType(discountType)
			.discountValue(discountValue)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();

		final int paymentAmount = amount - discountValue;
		final int expectedPointHeld = pointHeld - paymentAmount;

		// when
		final Payment result = paymentService.pay(order, point, issuedCoupon);

		// then
		assertThat(result.getAmount()).isEqualTo(paymentAmount);
		assertThat(point.getPoint()).isEqualTo(expectedPointHeld);
		assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

		assertThat(issuedCoupon.getOrderId()).isEqualTo(orderId);
		assertThat(issuedCoupon.getUsedAt()).isNotNull();
	}

	@DisplayName("정률할인 쿠폰으로 결제하면 쿠폰에 명시된 할인비율만큼 차감된 금액으로 결제한다.")
	@Test
	void payWithRateCoupon() throws Exception {
		// given
		final Long orderId = 1L;
		final int amount = 10000;
		final Order order = Order.builder()
			.amount(amount)
			.status(OrderStatus.ORDERED)
			.build();
		EntityIdSetter.setId(order, orderId);

		final int pointHeld = 10000;
		final Point point = Point.builder()
			.point(pointHeld)
			.build();

		final DiscountType discountType = DiscountType.RATE;
		final int discountValue = 15;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountType(discountType)
			.discountValue(discountValue)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();

		final int paymentAmount = 8500;
		final int expectedPointHeld = pointHeld - paymentAmount;

		// when
		final Payment result = paymentService.pay(order, point, issuedCoupon);

		// then
		assertThat(result.getAmount()).isEqualTo(paymentAmount);
		assertThat(point.getPoint()).isEqualTo(expectedPointHeld);
		assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

		assertThat(issuedCoupon.getOrderId()).isEqualTo(orderId);
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

		final int discountValue = 100;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountType(DiscountType.FIXED)
			.discountValue(discountValue)
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

		final int discountValue = 100;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountType(DiscountType.FIXED)
			.discountValue(discountValue)
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

		final int discountValue = 100;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountType(DiscountType.FIXED)
			.discountValue(discountValue)
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

		final int discountValue = 100;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.discountType(DiscountType.FIXED)
			.discountValue(discountValue)
			.expiredAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();

		// when & then
		assertThatThrownBy(() -> paymentService.pay(order, point, issuedCoupon))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(ORDER_ALREADY_PAID.getMessage());
	}
}
