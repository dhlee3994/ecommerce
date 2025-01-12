package io.hhplus.ecommerce.order.domain;

import static io.hhplus.ecommerce.global.exception.ErrorCode.DISCOUNT_AMOUNT_IS_LARGER_THAN_AMOUNT;
import static io.hhplus.ecommerce.global.exception.ErrorCode.ORDER_ALREADY_PAID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.SQLRestriction;

import io.hhplus.ecommerce.global.BaseEntity;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private int amount;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Builder
	private Order(final Long userId, final int amount, final OrderStatus status) {
		this.userId = userId;
		this.amount = amount;
		this.status = status;
	}

	public static Order createOrder(final Long userId) {
		return Order.builder()
			.userId(userId)
			.amount(0)
			.status(OrderStatus.ORDERED)
			.build();
	}

	public void addOrderItem(final OrderItem item) {
		this.amount += item.calculatePrice();
	}

	public int calculatePaymentPrice(int discountAmount) {
		if (this.amount < discountAmount) {
			throw new EcommerceException(DISCOUNT_AMOUNT_IS_LARGER_THAN_AMOUNT);
		}
		return this.amount - discountAmount;
	}

	public void updatePaymentStatus() {
		validate();
		this.status = OrderStatus.PAID;
	}

	public void validate() {
		if (this.status == OrderStatus.PAID) {
			throw new EcommerceException(ORDER_ALREADY_PAID);
		}
	}
}
