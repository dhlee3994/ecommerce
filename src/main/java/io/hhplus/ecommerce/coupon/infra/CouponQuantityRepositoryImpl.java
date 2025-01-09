package io.hhplus.ecommerce.coupon.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.coupon.domain.CouponQuantity;
import io.hhplus.ecommerce.coupon.domain.CouponQuantityRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CouponQuantityRepositoryImpl implements CouponQuantityRepository {

	private final CouponQuantityJpaRepository couponQuantityJpaRepository;

	@Override
	public Optional<CouponQuantity> findByCouponIdForUpdate(final long couponId) {
		return couponQuantityJpaRepository.findByCouponIdForUpdate(couponId);
	}
}
