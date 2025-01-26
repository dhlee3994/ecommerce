package io.hhplus.ecommerce.coupon.infra;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.coupon.domain.CouponQuantity;

@Profile("!optimistic-lock & !pessimistic-lock")
public interface CouponQuantityJpaRepository extends JpaRepository<CouponQuantity, Long> {

	Optional<CouponQuantity> findByCouponId(long couponId);

	@Query("select c from CouponQuantity c where c.couponId = :couponId")
	Optional<CouponQuantity> findByCouponIdForUpdate(long couponId);
}
