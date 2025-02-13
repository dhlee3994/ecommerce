package io.hhplus.ecommerce.payment.application;

import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_ALREADY_USED;
import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_IS_EXPIRED;
import static io.hhplus.ecommerce.global.exception.ErrorCode.ORDER_ALREADY_PAID;
import static io.hhplus.ecommerce.global.exception.ErrorCode.POINT_IS_NOT_ENOUGH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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

import io.hhplus.ecommerce.coupon.domain.DiscountType;
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
import io.hhplus.ecommerce.payment.domain.PaymentAmountCalculator;
import io.hhplus.ecommerce.payment.domain.PaymentRepository;
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
	private PaymentAmountCalculator paymentAmountCalculator;
	@Mock
	private DataPlatformClient dataPlatformClient;

	@DisplayName("정액할인 쿠폰으로 결제하면 쿠폰에 명시된 할인양만큼 차감된 금액으로 결제한다.")
	@Test
	void paymentWithFixedCoupon() throws Exception {
		// given
		final Long userId = 1L;
		given(userRepository.existsById(userId))
			.willReturn(true);

		final Long pointId = 1L;
		final int pointHeld = 10000;
		final Point point = Point.builder()
			.userId(userId)
			.point(pointHeld)
			.build();

		EntityIdSetter.setId(point, pointId);
		given(pointRepository.findByUserIdForUpdate(userId))
			.willReturn(Optional.of(point));

		final int orderAmount = 1000;
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
		final DiscountType discountType = DiscountType.FIXED;
		final int discountValue = 1000;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.userId(userId)
			.couponId(couponId)
			.discountType(discountType)
			.discountValue(discountValue)
			.expiredAt(LocalDateTime.now().plusDays(10))
			.build();

		given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
			.willReturn(Optional.of(issuedCoupon));

		final int expectedDiscountAmount = discountValue;
		final int expectedPaymentAmount = orderAmount - expectedDiscountAmount;

		given(paymentAmountCalculator.calculatePaymentAmount(order, issuedCoupon))
			.willReturn(expectedPaymentAmount);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(orderId)
			.userId(userId)
			.couponId(couponId)
			.build();

		// when
		final PaymentResponse result = paymentApplicationService.pay(request);

		// then
		assertThat(result).isNotNull()
			.extracting("orderId", "paymentPrice")
			.containsExactly(orderId, expectedPaymentAmount);

		then(paymentRepository).should(times(1)).save(argThat(payment ->
			payment.getOrderId().equals(orderId) && payment.getAmount() == expectedPaymentAmount
		));
		then(dataPlatformClient).should(times(1)).sendOrderData(any(OrderData.class));
	}

	@DisplayName("정률할인 쿠폰으로 결제하면 쿠폰에 명시된 할인비율만큼 차감된 금액으로 결제한다.")
	@Test
	void paymentWithRateCoupon() throws Exception {
		// given
		final Long userId = 1L;
		given(userRepository.existsById(userId))
			.willReturn(true);

		final Long pointId = 1L;
		final int pointHeld = 10000;
		final Point point = Point.builder()
			.userId(userId)
			.point(pointHeld)
			.build();

		EntityIdSetter.setId(point, pointId);
		given(pointRepository.findByUserIdForUpdate(userId))
			.willReturn(Optional.of(point));

		final int orderAmount = 10000;
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
		final DiscountType discountType = DiscountType.RATE;
		final int discountValue = 15;
		final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.userId(userId)
			.couponId(couponId)
			.discountType(discountType)
			.discountValue(discountValue)
			.expiredAt(LocalDateTime.now().plusDays(10))
			.build();

		given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
			.willReturn(Optional.of(issuedCoupon));

		final int expectedDiscountAmount = (orderAmount * discountValue / 100);
		final int expectedPaymentAmount = orderAmount - expectedDiscountAmount;

		given(paymentAmountCalculator.calculatePaymentAmount(order, issuedCoupon))
			.willReturn(expectedPaymentAmount);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(orderId)
			.userId(userId)
			.couponId(couponId)
			.build();

		// when
		final PaymentResponse result = paymentApplicationService.pay(request);

		// then
		assertThat(result).isNotNull()
			.extracting("orderId", "paymentPrice")
			.containsExactly(orderId, expectedPaymentAmount);

		then(paymentRepository).should(times(1)).save(argThat(payment ->
			payment.getOrderId().equals(orderId) && payment.getAmount() == expectedPaymentAmount
		));
		then(dataPlatformClient).should(times(1)).sendOrderData(any(OrderData.class));
	}

	@DisplayName("쿠폰을 적용하지 않으면 주문 총 금액을 결제한다.")
	@Test
	void paymentWithNoCoupon() throws Exception {
		// given
		final Long userId = 1L;
		given(userRepository.existsById(userId))
			.willReturn(true);

		final Long pointId = 1L;
		final int pointHeld = 10000;
		final Point point = Point.builder()
			.userId(userId)
			.point(pointHeld)
			.build();

		EntityIdSetter.setId(point, pointId);
		given(pointRepository.findByUserIdForUpdate(userId))
			.willReturn(Optional.of(point));

		final int orderAmount = 10000;
		final Long orderId = 1L;
		final Order order = Order.builder()
			.userId(userId)
			.amount(orderAmount)
			.status(OrderStatus.ORDERED)
			.build();

		EntityIdSetter.setId(order, orderId);
		given(orderRepository.findByIdForUpdate(order.getId()))
			.willReturn(Optional.of(order));

		final int expectedDiscountAmount = 0;
		final int expectedPaymentAmount = orderAmount - expectedDiscountAmount;

		given(paymentAmountCalculator.calculatePaymentAmount(eq(order), any(IssuedCoupon.class)))
			.willReturn(expectedPaymentAmount);

		final Long couponId = null;
		final PaymentRequest request = PaymentRequest.builder()
			.orderId(orderId)
			.userId(userId)
			.couponId(couponId)
			.build();

		// when
		final PaymentResponse result = paymentApplicationService.pay(request);

		// then
		assertThat(result).isNotNull()
			.extracting("orderId", "paymentPrice")
			.containsExactly(orderId, expectedPaymentAmount);

		then(paymentRepository).should(times(1)).save(argThat(payment ->
			payment.getOrderId().equals(orderId) && payment.getAmount() == expectedPaymentAmount)
		);
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
		final int discountValue = 0;

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
			.discountType(DiscountType.FIXED)
			.discountValue(discountValue)
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
			.given(paymentAmountCalculator)
			.calculatePaymentAmount(order, issuedCoupon);

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
		final int discountValue = 1000;

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
			.discountType(DiscountType.FIXED)
			.discountValue(discountValue)
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
			.given(paymentAmountCalculator)
			.calculatePaymentAmount(order, issuedCoupon);

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
		final int discountValue = 1000;

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
			.discountType(DiscountType.FIXED)
			.discountValue(discountValue)
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
			.given(paymentAmountCalculator)
			.calculatePaymentAmount(order, issuedCoupon);

		// when &&
		assertThatThrownBy(() -> paymentApplicationService.pay(request))
			.isInstanceOf(EcommerceException.class)
			.hasMessage(COUPON_ALREADY_USED.getMessage());
	}
}
