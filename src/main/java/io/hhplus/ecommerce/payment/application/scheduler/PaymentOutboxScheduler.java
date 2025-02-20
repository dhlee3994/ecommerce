package io.hhplus.ecommerce.payment.application.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.hhplus.ecommerce.payment.domain.outbox.OutboxStatus;
import io.hhplus.ecommerce.payment.domain.outbox.PaymentOutbox;
import io.hhplus.ecommerce.payment.domain.outbox.PaymentOutboxRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentOutboxScheduler {

	@Value("${outbox.max-batch-count}")
	private int maxBatchCount;

	@Value("${outbox.batch-size}")
	private int batchSize;

	@Value("${outbox.kafka-timeout-seconds}")
	private int kafkaTimeoutSeconds;

	@Value("${outbox.max-retry-count}")
	private int maxRetryCount;

	private final PaymentOutboxRepository paymentOutboxRepository;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final Pageable pageable;

	public PaymentOutboxScheduler(
		final PaymentOutboxRepository paymentOutboxRepository,
		final KafkaTemplate<String, String> kafkaTemplate
	) {
		this.paymentOutboxRepository = paymentOutboxRepository;
		this.kafkaTemplate = kafkaTemplate;
		this.pageable = PageRequest.of(0, 50);
	}

	@Scheduled(fixedDelay = 5000)
	public void processFailMessages() {
		try {
			int batchCount = 0;
			List<PaymentOutbox> pendingMessages;
			do {
				pendingMessages = paymentOutboxRepository.findAllByStatusAndRetryCountLessThan(
					OutboxStatus.PENDING,
					maxRetryCount,
					pageable
				);
				if (!pendingMessages.isEmpty()) {
					sendMessages(pendingMessages);
					batchCount++;
				}
			} while (!pendingMessages.isEmpty() && batchCount < maxBatchCount);

			log.info("Payment 아웃박스 배치 완료. 배치 횟수={}", batchCount);
		} catch (Exception e) {
			log.error("Payment 아웃박스 처리 중 오류 발생", e);
		}
	}

	private void sendMessages(List<PaymentOutbox> pendingMessages) {
		for (PaymentOutbox outbox : pendingMessages) {
			try {
				kafkaTemplate.send("payment_data_platform", outbox.getMessageKey(), outbox.getMessage());
				outbox.published();
				log.info("아웃박스 전송 완료. messageKey={}", outbox.getMessageKey());
			} catch (Exception e) {
				outbox.incrementRetryCount();

				if (outbox.getRetryCount() >= maxRetryCount) {
					outbox.failed();
					log.error("아웃박스 전송 재시도 횟수 초과. messageKey={}", outbox.getMessageKey());
				} else {
					log.warn("아웃박스 전송 실패. messageKey={}, Retry count={}", outbox.getMessageKey(),
						outbox.getRetryCount());
				}
			}
		}
		paymentOutboxRepository.saveAll(pendingMessages);
	}
}
