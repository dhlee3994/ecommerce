package io.hhplus.ecommerce.coupon.domain;

import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_ALREADY_USED;
import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_IS_EXPIRED;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import io.hhplus.ecommerce.global.BaseEntity;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
@AttributeOverride(name = "createdAt", column = @Column(name = "issued_at"))
@Table(name = "issued_coupon")
@Entity
public class IssuedCoupon extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private Long couponId;

	private Long orderId;

	private DiscountType discountType;

	private int discountValue;

	private LocalDateTime expiredAt;

	private LocalDateTime usedAt;

	@Builder
	private IssuedCoupon(
		final Long userId,
		final Long couponId,
		final Long orderId,
		final DiscountType discountType,
		final int discountValue,
		final LocalDateTime expiredAt,
		final LocalDateTime usedAt
	) {
		if (discountType == DiscountType.RATE && discountValue > 100) {
			throw new EcommerceException(ErrorCode.RATE_DISCOUNT_VALUE_OVER_100);
		}

		this.userId = userId;
		this.couponId = couponId;
		this.orderId = orderId;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.expiredAt = expiredAt;
		this.usedAt = usedAt;
	}

	public LocalDateTime getIssuedAt() {
		return this.getCreatedAt();
	}

	public static IssuedCoupon emptyCoupon() {
		return IssuedCoupon.builder()
			.discountType(DiscountType.NONE)
			.discountValue(0)
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

	public void use(final Long orderId, final LocalDateTime usedAt) {
		validate(usedAt);
		this.orderId = orderId;
		this.usedAt = usedAt;
	}
}
