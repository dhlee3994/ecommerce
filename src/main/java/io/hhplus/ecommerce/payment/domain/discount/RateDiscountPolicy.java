package io.hhplus.ecommerce.payment.domain.discount;

import org.springframework.stereotype.Component;

@Component
public class RateDiscountPolicy implements DiscountPolicy {

	@Override
	public int calculateDiscountAmount(final int price, final int discountValue) {
		return price * discountValue / 100;
	}
}
