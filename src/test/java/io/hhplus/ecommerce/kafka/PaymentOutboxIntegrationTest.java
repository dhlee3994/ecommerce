package io.hhplus.ecommerce.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.ecommerce.common.IntegrationTest;
import io.hhplus.ecommerce.payment.domain.event.PaymentCompletedEvent;
import io.hhplus.ecommerce.payment.domain.outbox.OutboxStatus;
import io.hhplus.ecommerce.payment.domain.outbox.PaymentOutbox;
import io.hhplus.ecommerce.payment.infra.outbox.PaymentOutboxJpaRepository;

@TestPropertySource(properties = {
	"outbox.max-batch-count=2",
	"outbox.batch-size=10",
	"outbox.kafka-timeout-seconds=5",
	"outbox.max-retry-count=3"
})
public class PaymentOutboxIntegrationTest extends IntegrationTest {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@MockitoBean
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

	@BeforeEach
	void setUp() {
		paymentOutboxJpaRepository.deleteAll();
	}

	@DisplayName("결제완료 이벤트 발생 시 아웃박스에 저장되고 메시지가 전송된다.")
	@Test
	void paymentCompleted() throws Exception {
		// given
		final String messageKey = "messageKey";
		final long orderId = 1L;
		final long userId = 1L;
		final int amount = 100;
		final var paymentCompletedEvent = new PaymentCompletedEvent(messageKey, orderId, userId, amount);

		final String serializedMessage = objectMapper.writeValueAsString(paymentCompletedEvent);

		// when
		transactionTemplate.execute(status -> {
			applicationEventPublisher.publishEvent(paymentCompletedEvent);
			return null;
		});

		// then
		final List<PaymentOutbox> outboxes = paymentOutboxJpaRepository.findAll();
		assertThat(outboxes).hasSize(1)
			.extracting("messageKey", "message", "outboxStatus")
			.containsExactly(tuple(messageKey, serializedMessage, OutboxStatus.PENDING));

		verify(kafkaTemplate, times(1))
			.send(eq("payment_data_platform"), eq(messageKey), eq(serializedMessage));
	}
}
