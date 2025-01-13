package io.hhplus.ecommerce.coupon.domain;

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
@Table(name = "coupon")
@Entity
public class Coupon extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private int issueLimit;

	private int quantity;

	private DiscountType discountType;

	private int discountValue;

	private LocalDateTime expiredAt;

	@Builder
	private Coupon(
		final String name,
		final int issueLimit,
		final int quantity,
		final DiscountType discountType,
		final int discountValue,
		final LocalDateTime expiredAt
	) {
		if (discountType == DiscountType.RATE && discountValue > 100) {
			throw new EcommerceException(ErrorCode.RATE_DISCOUNT_VALUE_OVER_100);
		}

		this.name = name;
		this.issueLimit = issueLimit;
		this.quantity = quantity;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.expiredAt = expiredAt;
	}

	public void updateQuantity(final int quantity) {
		this.quantity = quantity;
	}
}
