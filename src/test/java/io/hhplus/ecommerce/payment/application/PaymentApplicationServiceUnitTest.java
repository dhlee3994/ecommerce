package io.hhplus.ecommerce.payment.application;

import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_ALREADY_USED;
import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_IS_EXPIRED;
import static io.hhplus.ecommerce.global.exception.ErrorCode.ORDER_ALREADY_PAID;
import static io.hhplus.ecommerce.global.exception.ErrorCode.POINT_IS_NOT_ENOUGH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.coupon.domain.IssuedCouponRepository;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.order.domain.OrderRepository;
import io.hhplus.ecommerce.order.domain.OrderStatus;
import io.hhplus.ecommerce.payment.application.request.PaymentRequest;
import io.hhplus.ecommerce.payment.application.response.PaymentResponse;
import io.hhplus.ecommerce.payment.domain.DataPlatformClient;
import io.hhplus.ecommerce.payment.domain.OrderData;
import io.hhplus.ecommerce.payment.domain.Payment;
import io.hhplus.ecommerce.payment.domain.PaymentRepository;
import io.hhplus.ecommerce.payment.domain.PaymentService;
import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.point.domain.PointRepository;
import io.hhplus.ecommerce.user.domain.UserRepository;
import io.hhplus.ecommerce.util.EntityIdSetter;

@ExtendWith(MockitoExtension.class)
class PaymentApplicationServiceUnitTest {

	@InjectMocks
	private PaymentApplicationService paymentApplicationService;

	@Mock
	private PaymentRepository paymentRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PointRepository pointRepository;
	@Mock
	private IssuedCouponRepository issuedCouponRepository;
	@Mock
	private PaymentService paymentService;
	@Mock
	private DataPlatformClient dataPlatformClient;

	@DisplayName("주문 총 금액에 쿠폰을 적용한 금액을 결제할 수 있다.")
	@Test
	void payment() throws Exception {
		// given
		final Long userId = 1L;
		given(userRepository.existsById(userId))
			.willReturn(true);

		final int pointHeld = 10000;
		final int orderAmount = 1000;
		final int discountAmount = 1000;

		final int expectedPaymentPrice = orderAmount - discountAmount;

		final Long pointId = 1L;
		final Point point = Point.builder()
			.userId(userId)
			.point(pointHeld)
			.build();

		EntityIdSetter.setId(point, pointId);
		given(pointRepository.findByUserIdForUpdate(userId))
			.willReturn(Optional.of(point));

		final Long orderId = 1L;
		final Order order = Order.builder()
			.userId(userId)
			.amount(orderAmount)
			.status(OrderStatus.ORDERED)
			.build();

		EntityIdSetter.setId(order, orderId);
		given(orderRepository.findByIdForUpdate(order.getId()))
			.willReturn(Optional.of(order));

		final Long couponId = 1L;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.userId(userId)
			.couponId(couponId)
			.discountAmount(discountAmount)
			.expiredAt(LocalDateTime.now().plusDays(10))
			.build();

		given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
			.willReturn(Optional.of(issuedCoupon));

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(orderId)
			.userId(userId)
			.couponId(couponId)
			.build();

		final Long paymentId = 1L;
		final Payment payment = Payment.builder()
			.orderId(orderId)
			.amount(expectedPaymentPrice)
			.build();

		EntityIdSetter.setId(payment, paymentId);
		given(paymentService.pay(order, point, issuedCoupon))
			.willReturn(payment);

		// when
		final PaymentResponse result = paymentApplicationService.pay(request);

		// then
		assertThat(result).isNotNull()
			.extracting("orderId", "paymentPrice")
			.containsExactly(orderId, expectedPaymentPrice);

		then(paymentRepository).should(times(1)).save(payment);
		then(dataPlatformClient).should(times(1)).sendOrderData(any(OrderData.class));
	}

	@DisplayName("보유 포인트가 최종 결제 금액보다 적으면 EcommerceException 예외가 발생한다.")
	@Test
	void paymentOverPointHeld() throws Exception {
		// given
		final Long userId = 1L;
		given(userRepository.existsById(userId))
			.willReturn(true);

		final int pointHeld = 10000;
		final int orderAmount = pointHeld + 1;
		final int discountAmount = 0;

		final Long pointId = 1L;
		final Point point = Point.builder()
			.userId(userId)
			.point(pointHeld)
			.build();

		EntityIdSetter.setId(point, pointId);
		given(pointRepository.findByUserIdForUpdate(userId))
			.willReturn(Optional.of(point));

		final Long orderId = 1L;
		final Order order = Order.builder()
			.userId(userId)
			.amount(orderAmount)
			.status(OrderStatus.ORDERED)
			.build();

		EntityIdSetter.setId(order, orderId);
		given(orderRepository.findByIdForUpdate(order.getId()))
			.willReturn(Optional.of(order));

		final Long couponId = 1L;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.userId(userId)
			.couponId(couponId)
			.discountAmount(discountAmount)
			.expiredAt(LocalDateTime.now().plusDays(10))
			.build();

		given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
			.willReturn(Optional.of(issuedCoupon));

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(orderId)
			.userId(userId)
			.couponId(couponId)
			.build();

		willThrow(new EcommerceException(POINT_IS_NOT_ENOUGH))
			.given(paymentService)
			.pay(order, point, issuedCoupon);

		// when &&
		assertThatThrownBy(() -> paymentApplicationService.pay(request))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(POINT_IS_NOT_ENOUGH.getMessage());
	}

	@DisplayName("이미 결제한 주문에 대해 결제를 시도하면 EcommerceException 예외가 발생한다.")
	@Test
	void paymentAlreadyPaid() throws Exception {
		// given
		final Long userId = 1L;
		given(userRepository.existsById(userId))
			.willReturn(true);

		final Long orderId = 1L;
		final Order order = Order.builder()
			.userId(userId)
			.amount(1000)
			.status(OrderStatus.PAID)
			.build();

		EntityIdSetter.setId(order, orderId);
		given(orderRepository.findByIdForUpdate(order.getId()))
			.willReturn(Optional.of(order));

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(orderId)
			.userId(userId)
			.couponId(1L)
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
		final Long userId = 1L;
		given(userRepository.existsById(userId))
			.willReturn(true);

		final int pointHeld = 10000;
		final int orderAmount = 1000;
		final int discountAmount = 1000;

		final Long pointId = 1L;
		final Point point = Point.builder()
			.userId(userId)
			.point(pointHeld)
			.build();
		EntityIdSetter.setId(point, pointId);
		given(pointRepository.findByUserIdForUpdate(userId))
			.willReturn(Optional.of(point));

		final Long orderId = 1L;
		final Order order = Order.builder()
			.userId(userId)
			.amount(orderAmount)
			.status(OrderStatus.ORDERED)
			.build();
		EntityIdSetter.setId(order, orderId);
		given(orderRepository.findByIdForUpdate(order.getId()))
			.willReturn(Optional.of(order));

		final Long couponId = 1L;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.userId(userId)
			.couponId(couponId)
			.discountAmount(discountAmount)
			.expiredAt(LocalDateTime.now().minusMinutes(1))
			.build();

		given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
			.willReturn(Optional.of(issuedCoupon));

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(orderId)
			.userId(userId)
			.couponId(couponId)
			.build();

		willThrow(new EcommerceException(COUPON_IS_EXPIRED))
			.given(paymentService)
			.pay(order, point, issuedCoupon);

		// when &&
		assertThatThrownBy(() -> paymentApplicationService.pay(request))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(COUPON_IS_EXPIRED.getMessage());
	}

	@DisplayName("이미 사용한 쿠폰으로 결제를 시도하면 EcommerceException 예외가 발생한다.")
	@Test
	void paymentAlreadyUseCoupon() throws Exception {
		// given
		final Long userId = 1L;
		given(userRepository.existsById(userId))
			.willReturn(true);

		final int pointHeld = 10000;
		final int orderAmount = 1000;
		final int discountAmount = 1000;

		final Long pointId = 1L;
		final Point point = Point.builder()
			.userId(userId)
			.point(pointHeld)
			.build();
		EntityIdSetter.setId(point, pointId);
		given(pointRepository.findByUserIdForUpdate(userId))
			.willReturn(Optional.of(point));

		final Long orderId = 1L;
		final Order order = Order.builder()
			.userId(userId)
			.amount(orderAmount)
			.status(OrderStatus.ORDERED)
			.build();
		EntityIdSetter.setId(order, orderId);
		given(orderRepository.findByIdForUpdate(order.getId()))
			.willReturn(Optional.of(order));

		final Long couponId = 1L;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.userId(userId)
			.couponId(couponId)
			.discountAmount(discountAmount)
			.expiredAt(LocalDateTime.now().plusDays(10))
			.usedAt(LocalDateTime.now().minusDays(1))
			.build();

		given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
			.willReturn(Optional.of(issuedCoupon));

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(orderId)
			.userId(userId)
			.couponId(couponId)
			.build();

		willThrow(new EcommerceException(COUPON_ALREADY_USED))
			.given(paymentService)
			.pay(order, point, issuedCoupon);

		// when &&
		assertThatThrownBy(() -> paymentApplicationService.pay(request))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(COUPON_ALREADY_USED.getMessage());
	}
}
