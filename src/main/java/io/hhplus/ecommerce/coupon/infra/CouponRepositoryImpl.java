package io.hhplus.ecommerce.coupon.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.coupon.domain.Coupon;
import io.hhplus.ecommerce.coupon.domain.CouponRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CouponRepositoryImpl implements CouponRepository {

	private final CouponJpaRepository couponJpaRepository;

	@Override
	public Optional<Coupon> findById(final long couponId) {
		return couponJpaRepository.findById(couponId);
	}
}
