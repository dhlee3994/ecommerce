package io.hhplus.ecommerce.payment.presentation.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.payment.domain.event.PaymentCompletedEvent;
import io.hhplus.ecommerce.payment.domain.outbox.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentEventConsumer {

	private final PaymentOutboxRepository paymentOutboxRepository;
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = "payment_data_platform", groupId = "payment-group")
	public void consume(final String message) {
		log.info("consumed message={}", message);

		try {
			final var event = objectMapper.readValue(message, PaymentCompletedEvent.class);
			log.info("PaymentEvent={}", event);

			paymentOutboxRepository.findByMessageKey(event.messageKey())
				.ifPresent(paymentOutbox -> {
					paymentOutbox.published();
					paymentOutboxRepository.save(paymentOutbox);
					log.info("PaymentOutbox 처리 완료. messageKey={}", paymentOutbox.getMessageKey());
				});

		} catch (final Exception e) {
			log.error("Json 역직렬화 실패", e);
			throw new EcommerceException("Json 역직렬화 실패");
		}
	}
}
