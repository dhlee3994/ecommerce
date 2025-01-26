package io.hhplus.ecommerce.coupon.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.coupon.domain.CouponQuantity;

@Profile("pessimistic-lock")
public interface PessimisticCouponQuantityJpaRepository extends CouponQuantityJpaRepository {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from CouponQuantity c where c.couponId = :couponId")
	Optional<CouponQuantity> findByCouponIdForUpdate(long couponId);
}
