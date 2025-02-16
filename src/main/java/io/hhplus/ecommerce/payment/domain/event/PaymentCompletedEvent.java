package io.hhplus.ecommerce.payment.domain.event;

import io.hhplus.ecommerce.order.domain.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PaymentCompletedEvent {
	private final Long orderId;
	private final Long userId;
	private final int amount;

	public static PaymentCompletedEvent from(final Order order) {
		return new PaymentCompletedEvent(order.getId(), order.getUserId(), order.getAmount());
	}
}
