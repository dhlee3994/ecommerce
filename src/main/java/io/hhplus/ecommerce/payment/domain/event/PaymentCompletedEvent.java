package io.hhplus.ecommerce.payment.domain.event;

import java.util.UUID;

import io.hhplus.ecommerce.order.domain.Order;

public record PaymentCompletedEvent(
	String messageKey,
	Long orderId,
	Long userId,
	int amount
) {

	public static PaymentCompletedEvent from(final Order order) {
		return new PaymentCompletedEvent(
			UUID.randomUUID().toString(),
			order.getId(),
			order.getUserId(),
			order.getAmount()
		);
	}
}
