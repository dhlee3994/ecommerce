package io.hhplus.ecommerce.coupon.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.coupon.domain.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

	private final IssuedCouponJpaRepository issuedCouponJpaRepository;

	@Override
	public IssuedCoupon save(final IssuedCoupon issuedCoupon) {
		return issuedCouponJpaRepository.save(issuedCoupon);
	}

	@Override
	public Optional<IssuedCoupon> findByCouponIdAndUserIdForUpdate(final Long couponId, final Long userId) {
		if (couponId == null) {
			return Optional.of(IssuedCoupon.emptyCoupon());
		}
		return issuedCouponJpaRepository.findByCouponIdAndUserIdForUpdate(couponId, userId);
	}
}
