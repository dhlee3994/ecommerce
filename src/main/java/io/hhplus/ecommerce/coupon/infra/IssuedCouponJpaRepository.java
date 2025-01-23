package io.hhplus.ecommerce.coupon.infra;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;

@Profile("!optimistic-lock & !pessimistic-lock")
public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {

	@Query("select i from IssuedCoupon i where i.couponId = :couponId and i.userId = :userId")
	Optional<IssuedCoupon> findByCouponIdAndUserIdForUpdate(Long couponId, Long userId);
}
