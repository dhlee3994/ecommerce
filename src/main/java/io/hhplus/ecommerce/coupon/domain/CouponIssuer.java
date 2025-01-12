package io.hhplus.ecommerce.coupon.domain;

import org.springframework.stereotype.Service;

import io.hhplus.ecommerce.user.domain.User;

@Service
public class CouponIssuer {

	public IssuedCoupon issue(final User user, final Coupon coupon, final CouponQuantity couponQuantity) {
		couponQuantity.issueCoupon();
		coupon.updateQuantity(couponQuantity.getQuantity());

		return IssuedCoupon.builder()
			.userId(user.getId())
			.couponId(coupon.getId())
			.discountType(coupon.getDiscountType())
			.discountValue(coupon.getDiscountValue())
			.expiredAt(coupon.getExpiredAt())
			.build();
	}
}
