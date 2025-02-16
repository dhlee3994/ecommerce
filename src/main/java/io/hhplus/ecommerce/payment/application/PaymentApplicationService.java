package io.hhplus.ecommerce.payment.application;

import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_NOT_FOUND;
import static io.hhplus.ecommerce.global.exception.ErrorCode.ORDER_NOT_FOUND;
import static io.hhplus.ecommerce.global.exception.ErrorCode.POINT_NOT_FOUND;
import static io.hhplus.ecommerce.global.exception.ErrorCode.USER_NOT_FOUND;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.coupon.domain.IssuedCouponRepository;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.order.domain.OrderRepository;
import io.hhplus.ecommerce.payment.application.request.PaymentRequest;
import io.hhplus.ecommerce.payment.application.response.PaymentResponse;
import io.hhplus.ecommerce.payment.domain.Payment;
import io.hhplus.ecommerce.payment.domain.PaymentAmountCalculator;
import io.hhplus.ecommerce.payment.domain.PaymentRepository;
import io.hhplus.ecommerce.payment.domain.event.PaymentCompletedEvent;
import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.point.domain.PointRepository;
import io.hhplus.ecommerce.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentApplicationService {

	private final PaymentAmountCalculator paymentAmountCalculator;

	private final PaymentRepository paymentRepository;

	private final UserRepository userRepository;
	private final PointRepository pointRepository;

	private final OrderRepository orderRepository;

	private final IssuedCouponRepository issuedCouponRepository;

	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public PaymentResponse pay(final PaymentRequest request) {
		if (!userRepository.existsById(request.getUserId())) {
			throw new EntityNotFoundException(USER_NOT_FOUND.getMessage());
		}

		final Order order = orderRepository.findByIdForUpdate(request.getOrderId())
			.orElseThrow(() -> new EntityNotFoundException(ORDER_NOT_FOUND.getMessage()));

		order.validate();

		final IssuedCoupon issuedCoupon = request.getCouponId() == null
			? IssuedCoupon.emptyCoupon()
			: issuedCouponRepository.findByCouponIdAndUserIdForUpdate(request.getCouponId(), request.getUserId())
			.orElseThrow(() -> new EntityNotFoundException(COUPON_NOT_FOUND.getMessage()));

		final Point point = pointRepository.findByUserIdForUpdate(request.getUserId())
			.orElseThrow(() -> new EntityNotFoundException(POINT_NOT_FOUND.getMessage()));

		final int paymentAmount = paymentAmountCalculator.calculatePaymentAmount(order, issuedCoupon);
		Payment payment = Payment.builder()
			.orderId(order.getId())
			.amount(paymentAmount)
			.build();

		paymentRepository.save(payment);

		issuedCoupon.use(order.getId(), LocalDateTime.now());
		point.use(paymentAmount);
		order.updatePaymentStatus();

		eventPublisher.publishEvent(PaymentCompletedEvent.from(order));

		return PaymentResponse.from(payment);
	}
}
