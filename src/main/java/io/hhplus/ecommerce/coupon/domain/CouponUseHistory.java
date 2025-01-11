package io.hhplus.ecommerce.coupon.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupon_use_history")
@Entity
public class CouponUseHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long issuedCouponId;

	private Long orderId;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime usedAt;

	@Builder
	private CouponUseHistory(final Long issuedCouponId, final Long orderId, final LocalDateTime usedAt) {
		this.issuedCouponId = issuedCouponId;
		this.orderId = orderId;
		this.usedAt = usedAt;
	}
}
