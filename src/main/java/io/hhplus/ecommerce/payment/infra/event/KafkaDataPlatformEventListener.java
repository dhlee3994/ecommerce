package io.hhplus.ecommerce.payment.infra.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.payment.domain.event.PaymentCompletedEvent;
import io.hhplus.ecommerce.payment.domain.event.PaymentCompletedEventListener;
import io.hhplus.ecommerce.payment.domain.outbox.PaymentOutbox;
import io.hhplus.ecommerce.payment.domain.outbox.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaDataPlatformEventListener implements PaymentCompletedEventListener {

	private final PaymentOutboxRepository paymentOutboxRepository;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	@Override
	public void saveOutbox(PaymentCompletedEvent paymentCompletedEvent) {
		try {
			final String payload = objectMapper.writeValueAsString(paymentCompletedEvent);
			paymentOutboxRepository.save(PaymentOutbox.of(paymentCompletedEvent.messageKey(), payload));
		} catch (JsonProcessingException e) {
			log.error("Save Payment Outbox failed.", e);
			throw new EcommerceException("Save Payment Outbox failed.");
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Override
	public void sendMessage(final PaymentCompletedEvent paymentCompletedEvent) {
		try {
			log.info("send order data. {}", paymentCompletedEvent);
			kafkaTemplate.send(
				"payment_data_platform",
				paymentCompletedEvent.messageKey(),
				objectMapper.writeValueAsString(paymentCompletedEvent)
			);
			log.info("전송완료!. {}", paymentCompletedEvent);
		} catch (Exception e) {
			log.error("Failed to send OrderData. messageKey={}", paymentCompletedEvent.messageKey(), e);
		}
	}
}
