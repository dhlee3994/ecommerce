package io.hhplus.ecommerce.payment.domain.outbox;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

public interface PaymentOutboxRepository {

	PaymentOutbox save(PaymentOutbox paymentOutbox);

	void saveAll(List<PaymentOutbox> pendingMessages);

	List<PaymentOutbox> findAllByStatusAndRetryCountLessThan(OutboxStatus outboxStatus, int retryCount, Pageable pageable);

	Optional<PaymentOutbox> findByMessageKey(String messageKey);
}
