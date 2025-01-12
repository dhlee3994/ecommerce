package io.hhplus.ecommerce.payment.application;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import io.hhplus.ecommerce.coupon.domain.DiscountType;
import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.coupon.infra.IssuedCouponJpaRepository;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.order.domain.OrderStatus;
import io.hhplus.ecommerce.order.infra.OrderJpaRepository;
import io.hhplus.ecommerce.payment.application.request.PaymentRequest;
import io.hhplus.ecommerce.payment.application.response.PaymentResponse;
import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.point.infra.PointJpaRepository;
import io.hhplus.ecommerce.product.infra.StockJpaRepository;
import io.hhplus.ecommerce.user.domain.User;
import io.hhplus.ecommerce.user.infrastructure.UserJpaRepository;

@ActiveProfiles("testcontainers")
@ImportTestcontainers(TestcontainersConfiguration.class)
@SpringBootTest
class PaymentApplicationServiceIntegrationTest {

	@Autowired
	private PaymentApplicationService paymentApplicationService;

	@Autowired
	private OrderJpaRepository orderJpaRepository;
	@Autowired
	private StockJpaRepository stockJpaRepository;
	@Autowired
	private UserJpaRepository userJpaRepository;
	@Autowired
	private PointJpaRepository pointJpaRepository;
	@Autowired
	private IssuedCouponJpaRepository issuedCouponJpaRepository;

	@BeforeEach
	void setUp() {
		orderJpaRepository.deleteAllInBatch();
		stockJpaRepository.deleteAllInBatch();
		userJpaRepository.deleteAllInBatch();
		pointJpaRepository.deleteAllInBatch();
		issuedCouponJpaRepository.deleteAllInBatch();
	}

	@DisplayName("정액할인 쿠폰으로 결제하면 쿠폰에 명시된 할인양만큼 차감된 금액으로 결제한다.")
	@Test
	void paymentWithFixedCoupon() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());

		final Long userId = user.getId();
		final int pointHeld = 10000;
		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final int orderAmount = 1000;
		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(OrderStatus.ORDERED)
				.build()
		);

		final long couponId = 1L;
		final DiscountType discountType = DiscountType.FIXED;
		final int discountValue = 1000;
		final IssuedCoupon issuedCoupon = issuedCouponJpaRepository.save(
			IssuedCoupon.builder()
				.userId(userId)
				.couponId(couponId)
				.discountType(discountType)
				.discountValue(discountValue)
				.expiredAt(LocalDateTime.now().plusDays(10))
				.build()
		);

		final int expectedPaymentPrice = orderAmount - discountValue;
		final int expectedPointHeld = pointHeld - expectedPaymentPrice;

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(couponId)
			.build();

		// when
		final PaymentResponse result = paymentApplicationService.pay(request);

		// then
		assertThat(result).isNotNull()
			.extracting("orderId", "paymentPrice")
			.containsExactly(order.getId(), expectedPaymentPrice);

		assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedPointHeld);
		assertThat(orderJpaRepository.findById(order.getId()).get().getStatus()).isEqualTo(OrderStatus.PAID);

		final IssuedCoupon afterIssuedCoupon = issuedCouponJpaRepository.findById(issuedCoupon.getId()).get();
		assertThat(afterIssuedCoupon.getOrderId()).isEqualTo(order.getId());
		assertThat(afterIssuedCoupon.getUsedAt()).isNotNull();
	}

	@DisplayName("정률할인 쿠폰으로 결제하면 쿠폰에 명시된 할인비율만큼 차감된 금액으로 결제한다.")
	@Test
	void paymentWithRateCoupon() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());

		final Long userId = user.getId();
		final int pointHeld = 10000;
		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final int orderAmount = 10000;
		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(OrderStatus.ORDERED)
				.build()
		);

		final long couponId = 1L;
		final DiscountType discountType = DiscountType.RATE;
		final int discountValue = 15;
		final IssuedCoupon issuedCoupon = issuedCouponJpaRepository.save(
			IssuedCoupon.builder()
				.userId(userId)
				.couponId(couponId)
				.discountType(discountType)
				.discountValue(discountValue)
				.expiredAt(LocalDateTime.now().plusDays(10))
				.build()
		);

		final int expectedDiscountAmount = (orderAmount * discountValue / 100);
		final int expectedPaymentAmount = orderAmount - expectedDiscountAmount;
		final int expectedPointHeld = pointHeld - expectedPaymentAmount;

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(couponId)
			.build();

		// when
		final PaymentResponse result = paymentApplicationService.pay(request);

		// then
		assertThat(result).isNotNull()
			.extracting("orderId", "paymentPrice")
			.containsExactly(order.getId(), expectedPaymentAmount);

		assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedPointHeld);
		assertThat(orderJpaRepository.findById(order.getId()).get().getStatus()).isEqualTo(OrderStatus.PAID);

		final IssuedCoupon afterIssuedCoupon = issuedCouponJpaRepository.findById(issuedCoupon.getId()).get();
		assertThat(afterIssuedCoupon.getOrderId()).isEqualTo(order.getId());
		assertThat(afterIssuedCoupon.getUsedAt()).isNotNull();
	}

	@DisplayName("쿠폰을 적용하지 않으면 주문 총 금액을 결제한다.")
	@Test
	void paymentWithNoCoupon() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Long userId = user.getId();

		final int pointHeld = 10000;
		final int orderAmount = 1000;

		final int expectedPaymentPrice = orderAmount;
		final int expectedPointHeld = pointHeld - expectedPaymentPrice;

		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(OrderStatus.ORDERED)
				.build()
		);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(null)
			.build();

		// when
		final PaymentResponse result = paymentApplicationService.pay(request);

		// then
		assertThat(result).isNotNull()
			.extracting("orderId", "paymentPrice")
			.containsExactly(order.getId(), expectedPaymentPrice);

		assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedPointHeld);
		assertThat(orderJpaRepository.findById(order.getId()).get().getStatus()).isEqualTo(OrderStatus.PAID);
	}

	@DisplayName("보유 포인트가 최종 결제 금액보다 적으면 EcommerceException 예외가 발생한다.")
	@Test
	void paymentOverPointHeld() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Long userId = user.getId();

		final int pointHeld = 1000;
		final int orderAmount = pointHeld + 1;
		final int discountValue = 0;

		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(OrderStatus.ORDERED)
				.build()
		);

		final long couponId = 1L;
		issuedCouponJpaRepository.save(
			IssuedCoupon.builder()
				.userId(userId)
				.couponId(couponId)
				.discountType(DiscountType.FIXED)
				.discountValue(discountValue)
				.expiredAt(LocalDateTime.now().plusDays(10))
				.build()
		);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(couponId)
			.build();

		// when &&
		assertThatThrownBy(() -> paymentApplicationService.pay(request))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(POINT_IS_NOT_ENOUGH.getMessage());
	}

	@DisplayName("이미 결제한 주문에 대해 결제를 시도하면 EcommerceException 예외가 발생한다.")
	@Test
	void paymentAlreadyPaid() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Long userId = user.getId();

		final int pointHeld = 1000;
		final int orderAmount = pointHeld + 1;
		final int discountValue = 0;

		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final OrderStatus paid = OrderStatus.PAID;
		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(paid)
				.build()
		);

		final long couponId = 1L;
		issuedCouponJpaRepository.save(
			IssuedCoupon.builder()
				.userId(userId)
				.couponId(couponId)
				.discountType(DiscountType.FIXED)
				.discountValue(discountValue)
				.expiredAt(LocalDateTime.now().plusDays(10))
				.build()
		);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(couponId)
			.build();

		// when &&
		assertThatThrownBy(() -> paymentApplicationService.pay(request))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(ORDER_ALREADY_PAID.getMessage());
	}

	@DisplayName("만료된 쿠폰으로 결제를 시도하면 EcommerceException 예외가 발생한다.")
	@Test
	void paymentExpiredCoupon() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Long userId = user.getId();

		final int pointHeld = 1000;
		final int orderAmount = pointHeld + 1;
		final int discountValue = 0;

		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(OrderStatus.ORDERED)
				.build()
		);

		final long couponId = 1L;
		issuedCouponJpaRepository.save(
			IssuedCoupon.builder()
				.userId(userId)
				.couponId(couponId)
				.discountType(DiscountType.FIXED)
				.discountValue(discountValue)
				.expiredAt(LocalDateTime.now().minusMinutes(1))
				.build()
		);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(couponId)
			.build();

		// when &&
		assertThatThrownBy(() -> paymentApplicationService.pay(request))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(COUPON_IS_EXPIRED.getMessage());
	}

	@DisplayName("이미 사용한 쿠폰으로 결제를 시도하면 EcommerceException 예외가 발생한다.")
	@Test
	void paymentAlreadyUseCoupon() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Long userId = user.getId();

		final int pointHeld = 1000;
		final int orderAmount = pointHeld + 1;
		final int discountValue = 0;

		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(OrderStatus.ORDERED)
				.build()
		);

		final long couponId = 1L;
		issuedCouponJpaRepository.save(
			IssuedCoupon.builder()
				.userId(userId)
				.couponId(couponId)
				.discountType(DiscountType.FIXED)
				.discountValue(discountValue)
				.expiredAt(LocalDateTime.now().plusDays(10))
				.usedAt(LocalDateTime.now().minusMinutes(1))
				.build()
		);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(couponId)
			.build();

		// when &&
		assertThatThrownBy(() -> paymentApplicationService.pay(request))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(COUPON_ALREADY_USED.getMessage());
	}
}
