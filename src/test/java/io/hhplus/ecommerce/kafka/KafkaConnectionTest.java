package io.hhplus.ecommerce.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import io.hhplus.ecommerce.common.IntegrationTest;

public class KafkaConnectionTest extends IntegrationTest {

	private final AtomicReference<String> receiveMessage = new AtomicReference<>();

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@DisplayName("카프카 프로듀서와 컨슈머에 정상 연결할 수 있다.")
	@Test
	void connection() throws Exception {
		// given
		final String message = "Hello World!";

		// when
		kafkaTemplate.send("test-topic", message);

		// then
		await().atMost(Duration.ofSeconds(5))
			.untilAsserted(() -> {
				assertThat(receiveMessage.get()).isEqualTo(message);
			});
	}

	// TODO: 카프카 리스너 죽이기 + topics,groupId 변수화
	@KafkaListener(topics = "test-topic", groupId = "test-group")
	public void listen(String message) {
		this.receiveMessage.set(message);
	}
}
