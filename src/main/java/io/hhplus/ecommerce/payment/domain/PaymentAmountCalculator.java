package io.hhplus.ecommerce.payment.domain;

import org.springframework.stereotype.Service;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.payment.domain.discount.DiscountCalculator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentAmountCalculator {

	private final DiscountCalculator discountCalculator;

	public int calculatePaymentAmount(final Order order, final IssuedCoupon issuedCoupon) {
		final int discountAmount = discountCalculator.calculateDiscountAmount(order, issuedCoupon);
		return order.calculatePaymentPrice(discountAmount);
	}
}
