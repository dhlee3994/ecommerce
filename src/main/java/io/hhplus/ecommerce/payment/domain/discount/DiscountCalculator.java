package io.hhplus.ecommerce.payment.domain.discount;

import org.springframework.stereotype.Service;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.order.domain.Order;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DiscountCalculator {

	private final DiscountPolicyFactory discountPolicyFactory;

	public int calculateDiscountAmount(final Order order, final IssuedCoupon issuedCoupon) {
		return discountPolicyFactory.getDiscountPolicy(issuedCoupon.getDiscountType())
			.calculateDiscountAmount(order.getAmount(), issuedCoupon.getDiscountValue());
	}
}
