package io.hhplus.ecommerce.payment.domain.outbox;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_outbox")
@Entity
public class PaymentOutbox {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String messageKey;

	private String message;

	@Enumerated(EnumType.STRING)
	private OutboxStatus outboxStatus;

	private LocalDateTime createdAt;
	private LocalDateTime publishedAt;

	private int retryCount;

	private PaymentOutbox(
		final String messageKey,
		final String message,
		final OutboxStatus outboxStatus,
		final LocalDateTime createdAt,
		final int retryCount
	) {
		this.messageKey = messageKey;
		this.message = message;
		this.outboxStatus = outboxStatus;
		this.createdAt = createdAt;
		this.retryCount = retryCount;
	}

	public static PaymentOutbox of(final String messageKey, final String message) {
		return new PaymentOutbox(
			messageKey,
			message,
			OutboxStatus.PENDING,
			LocalDateTime.now(),
			0
		);
	}

	public void published() {
		this.outboxStatus = OutboxStatus.PUBLISHED;
		this.publishedAt = LocalDateTime.now();
	}

	public void incrementRetryCount() {
		this.retryCount++;
	}

	public void failed() {
		this.outboxStatus = OutboxStatus.FAILED;
	}
}


