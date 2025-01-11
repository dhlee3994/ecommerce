package io.hhplus.ecommerce.coupon.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import io.hhplus.ecommerce.global.BaseEntity;
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

	private int discountAmount;

	private LocalDateTime expiredAt;

	@Builder
	private Coupon(
		final String name,
		final int issueLimit,
		final int quantity,
		final int discountAmount,
		final LocalDateTime expiredAt
	) {
		this.name = name;
		this.issueLimit = issueLimit;
		this.quantity = quantity;
		this.discountAmount = discountAmount;
		this.expiredAt = expiredAt;
	}

	public void updateQuantity(final int quantity) {
		this.quantity = quantity;
	}
}
