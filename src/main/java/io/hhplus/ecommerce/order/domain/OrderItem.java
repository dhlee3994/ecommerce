package io.hhplus.ecommerce.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.SQLRestriction;

import io.hhplus.ecommerce.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long orderId;

	@Column(nullable = false)
	private Long productId;

	@Column(length = 100, nullable = false)
	private String productName;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private int quantity;

	@Builder
	private OrderItem(
		final Long orderId, final Long productId, final String productName, final int price, final int quantity) {
		this.orderId = orderId;
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
	}

	public int calculatePrice() {
		return this.price * this.quantity;
	}
}
