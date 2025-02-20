package io.hhplus.ecommerce.payment.infra.outbox;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.payment.domain.outbox.OutboxStatus;
import io.hhplus.ecommerce.payment.domain.outbox.PaymentOutbox;
import io.hhplus.ecommerce.payment.domain.outbox.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

	private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;

	@Override
	public PaymentOutbox save(final PaymentOutbox paymentOutbox) {
		return paymentOutboxJpaRepository.save(paymentOutbox);
	}

	@Override
	public void saveAll(final List<PaymentOutbox> pendingMessages) {
		paymentOutboxJpaRepository.saveAll(pendingMessages);
	}

	@Override
	public List<PaymentOutbox> findAllByStatusAndRetryCountLessThan(
		final OutboxStatus outboxStatus,
		final int retryCount,
		final Pageable pageable
	) {
		return paymentOutboxJpaRepository.findAllByOutboxStatusAndRetryCountLessThan(outboxStatus, retryCount, pageable);
	}

	@Override
	public Optional<PaymentOutbox> findByMessageKey(final String messageKey) {
		return paymentOutboxJpaRepository.findByMessageKey(messageKey);
	}
}
