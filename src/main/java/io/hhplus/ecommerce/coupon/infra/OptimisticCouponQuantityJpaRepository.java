package io.hhplus.ecommerce.coupon.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.coupon.domain.CouponQuantity;

@Profile("optimistic-lock")
public interface OptimisticCouponQuantityJpaRepository extends CouponQuantityJpaRepository {

	@Lock(LockModeType.OPTIMISTIC)
	@Query("select c from CouponQuantity c where c.couponId = :couponId")
	Optional<CouponQuantity> findByCouponIdForUpdate(long couponId);
}
