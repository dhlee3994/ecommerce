package io.hhplus.ecommerce.coupon.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;

@Profile("optimistic-lock")
public interface OptimisticIssuedCouponJpaRepository extends IssuedCouponJpaRepository {

	@Lock(LockModeType.OPTIMISTIC)
	@Query("select i from IssuedCoupon i where i.couponId = :couponId and i.userId = :userId")
	Optional<IssuedCoupon> findByCouponIdAndUserIdForUpdate(Long couponId, Long userId);
}
