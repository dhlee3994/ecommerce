package io.hhplus.ecommerce.point.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import io.hhplus.ecommerce.point.application.request.PointChargeRequest;
import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.point.infra.PointJpaRepository;
import io.hhplus.ecommerce.user.domain.User;
import io.hhplus.ecommerce.user.infrastructure.UserJpaRepository;

@ActiveProfiles("testcontainers")
@ImportTestcontainers(TestcontainersConfiguration.class)
@SpringBootTest
class PointApplicationServiceConcurrencyTest {

	@Autowired
	private PointApplicationService pointApplicationService;

	@Autowired
	private PointJpaRepository pointJpaRepository;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@BeforeEach
	void setUp() {
		pointJpaRepository.deleteAllInBatch();
		userJpaRepository.deleteAllInBatch();
	}

	@DisplayName("한 명의 사용자가 동시에 50번의 충전 요청을 해도 모든 요청이 성공한다.")
	@Test
	void oneUserCharge50() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());

		final Long userId = user.getId();
		final int point = 0;
		pointJpaRepository.save(Point.builder().userId(userId).point(point).build());

		final int chargePoint = 1000;
		final int chargeTryCount = 50;
		final int expectedPoint = point + (chargePoint * chargeTryCount);

		final PointChargeRequest request = PointChargeRequest.builder()
			.userId(userId)
			.chargePoint(chargePoint)
			.build();

		final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(chargeTryCount);
		final AtomicInteger exceptionCount = new AtomicInteger(0);

		// when
		for (int i = 1; i <= chargeTryCount; i++) {
			tasks.add(CompletableFuture.supplyAsync(() -> {
				pointApplicationService.charge(request);
				return true;
			}).exceptionally(e -> {
				exceptionCount.incrementAndGet();
				return false;
			}));
		}

		// then
		CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

		int successCount = 0;
		int failureCount = 0;
		for (CompletableFuture<Boolean> task : tasks) {
			if (task.get()) {
				successCount++;
			} else {
				failureCount++;
			}
		}

		assertThat(exceptionCount.get()).isEqualTo(0);
		assertThat(successCount).isEqualTo(50);
		assertThat(failureCount).isEqualTo(exceptionCount.get());

		assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedPoint);
	}
}
