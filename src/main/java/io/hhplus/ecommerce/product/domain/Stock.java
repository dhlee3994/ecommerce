package io.hhplus.ecommerce.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "stock")
@Entity
public class Stock extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long productId;

	@Column(nullable = false)
	private int quantity;

	@Builder
	private Stock(final Long productId, final int quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}

	public void decrease(final int quantity) {
		if (this.quantity < quantity) {
			throw new EcommerceException(ErrorCode.STOCK_QUANTITY_NOT_ENOUGH);
		}
		this.quantity -= quantity;
	}
}
