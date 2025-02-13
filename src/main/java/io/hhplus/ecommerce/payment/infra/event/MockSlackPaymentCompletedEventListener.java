package io.hhplus.ecommerce.payment.infra.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.hhplus.ecommerce.payment.domain.event.PaymentCompletedEvent;
import io.hhplus.ecommerce.payment.domain.event.PaymentCompletedEventListener;
import io.hhplus.ecommerce.payment.domain.event.SlackPaymentCompletedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MockSlackPaymentCompletedEventListener implements PaymentCompletedEventListener {

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Override
	public void publish(final PaymentCompletedEvent paymentCompletedEvent) {
		try {
			log.info("send order data to slack. {}", SlackPaymentCompletedEvent.from(paymentCompletedEvent));
		} catch (Exception e) {
			log.error("Failed to send OrderData. orderId={}", paymentCompletedEvent.getOrderId(), e);
		}
	}
}
