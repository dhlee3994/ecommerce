package io.hhplus.ecommerce.coupon.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.coupon.domain.CouponQuantity;

public interface CouponQuantityJpaRepository extends JpaRepository<CouponQuantity, Long> {

	Optional<CouponQuantity> findByCouponId(long couponId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from CouponQuantity c where c.couponId = :couponId")
	Optional<CouponQuantity> findByCouponIdForUpdate(long couponId);
}
