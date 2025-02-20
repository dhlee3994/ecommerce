package io.hhplus.ecommerce.payment.infra.outbox;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.hhplus.ecommerce.payment.domain.outbox.OutboxStatus;
import io.hhplus.ecommerce.payment.domain.outbox.PaymentOutbox;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutbox, Long> {

	List<PaymentOutbox> findAllByOutboxStatusAndRetryCountLessThan(OutboxStatus outboxStatus, int retryCount, Pageable pageable);

	Optional<PaymentOutbox> findByMessageKey(String messageKey);
}
