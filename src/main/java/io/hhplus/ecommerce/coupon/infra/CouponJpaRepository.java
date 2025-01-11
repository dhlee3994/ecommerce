package io.hhplus.ecommerce.coupon.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import io.hhplus.ecommerce.coupon.domain.Coupon;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
