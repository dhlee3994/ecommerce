package io.hhplus.ecommerce.payment.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SlackPaymentCompletedEvent {

	private final Long orderId;
	private final Long userId;
	private final int amount;

	public static SlackPaymentCompletedEvent from(final PaymentCompletedEvent event) {
		return new SlackPaymentCompletedEvent(event.getOrderId(), event.getUserId(), event.getAmount());
	}
}
