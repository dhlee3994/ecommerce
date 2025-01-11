package io.hhplus.ecommerce.coupon.domain;

import java.util.Optional;

public interface IssuedCouponRepository {

	IssuedCoupon save(IssuedCoupon issuedCoupon);

	Optional<IssuedCoupon> findByCouponIdAndUserIdForUpdate(Long couponId, Long userId);
}
