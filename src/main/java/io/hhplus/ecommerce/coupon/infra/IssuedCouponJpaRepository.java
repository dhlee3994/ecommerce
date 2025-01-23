package io.hhplus.ecommerce.coupon.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select i from IssuedCoupon i where i.couponId = :couponId and i.userId = :userId")
	Optional<IssuedCoupon> findByCouponIdAndUserIdForUpdate(Long couponId, Long userId);
}
