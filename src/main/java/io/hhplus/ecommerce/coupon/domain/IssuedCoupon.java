package io.hhplus.ecommerce.coupon.domain;

import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_ALREADY_USED;
import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_IS_EXPIRED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.hhplus.ecommerce.global.exception.EcommerceException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at is null")
@Table(name = "issued_coupon")
@Entity
public class IssuedCoupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private Long couponId;

	private int discountAmount;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime issuedAt;

	private LocalDateTime expiredAt;

	private LocalDateTime usedAt;

	private LocalDateTime deletedAt;

	@Builder
	private IssuedCoupon(
		final Long id,
		final Long userId,
		final Long couponId,
		final int discountAmount,
		final LocalDateTime expiredAt,
		final LocalDateTime usedAt,
		final LocalDateTime deletedAt
	) {
		this.id = id;
		this.userId = userId;
		this.couponId = couponId;
		this.discountAmount = discountAmount;
		this.expiredAt = expiredAt;
		this.usedAt = usedAt;
		this.deletedAt = deletedAt;
	}

	public static IssuedCoupon emptyCoupon() {
		return IssuedCoupon.builder()
			.discountAmount(0)
			.expiredAt(LocalDateTime.MAX)
			.usedAt(null)
			.build();
	}

	private void validate(final LocalDateTime dateTime) {
		if (this.expiredAt.isBefore(dateTime)) {
			throw new EcommerceException(COUPON_IS_EXPIRED);
		}

		if (this.usedAt != null) {
			throw new EcommerceException(COUPON_ALREADY_USED);
		}
	}

	public void use(final LocalDateTime usedAt) {
		validate(usedAt);
		this.usedAt = usedAt;
	}
}
