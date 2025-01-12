package io.hhplus.ecommerce.payment.domain;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.payment.domain.discount.DiscountCalculator;
import io.hhplus.ecommerce.point.domain.Point;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentService {

	private final DiscountCalculator discountCalculator;

	public Payment pay(final Order order, final Point point, final IssuedCoupon issuedCoupon) {
		final LocalDateTime paymentAt = LocalDateTime.now();
		issuedCoupon.use(order.getId(), paymentAt);

		final int discountAmount = discountCalculator.calculateDiscountAmount(order, issuedCoupon);
		final int paymentAmount = order.calculatePaymentPrice(discountAmount);
		point.use(paymentAmount);

		order.updatePaymentStatus();

		return Payment.builder()
			.orderId(order.getId())
			.amount(paymentAmount)
			.build();
	}
}
