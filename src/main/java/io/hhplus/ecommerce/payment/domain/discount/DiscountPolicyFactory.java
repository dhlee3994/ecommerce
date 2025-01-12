package io.hhplus.ecommerce.payment.domain.discount;

import org.springframework.stereotype.Component;

import io.hhplus.ecommerce.coupon.domain.DiscountType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DiscountPolicyFactory {

	private final NoneDiscountPolicy noneDiscountPolicy;
	private final FixedDiscountPolicy fixedDiscountPolicy;
	private final RateDiscountPolicy rateDiscountPolicy;

	public DiscountPolicy getDiscountPolicy(final DiscountType discountType) {
		return switch (discountType) {
			case NONE -> noneDiscountPolicy;
			case FIXED -> fixedDiscountPolicy;
			case RATE -> rateDiscountPolicy;
		};
	}
}
