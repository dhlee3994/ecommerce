package io.hhplus.ecommerce.point.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import io.hhplus.ecommerce.common.ServiceIntegrationTest;
import io.hhplus.ecommerce.point.application.request.PointChargeRequest;
import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.user.domain.User;

//@ActiveProfiles("optimistic-lock")
@ActiveProfiles("pessimistic-lock")
class PointApplicationServiceConcurrencyTest extends ServiceIntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(PointApplicationServiceConcurrencyTest.class);

	@DisplayName("Lock의 유형별로 성능을 확인하기 위한 테스트이며, 동일한 사용자가 포인트 충전 요청을 X번 하면 X번 모두 성공한다.")
	@RepeatedTest(6)
	void oneUserChargeForReport() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());

		final Long userId = user.getId();
		final int point = 0;
		pointJpaRepository.save(Point.builder().userId(userId).point(point).build());

		final int chargePoint = 1000;
		final int tryCount = 5;
		final int expectedPoint = point + (chargePoint * tryCount);

		final PointChargeRequest request = PointChargeRequest.builder()
			.userId(userId)
			.chargePoint(chargePoint)
			.build();

		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(tryCount);

		final long startTime = System.currentTimeMillis();

		// when
		for (int i = 0; i < tryCount; i++) {
			executorService.execute(() -> {
				try {
					pointApplicationService.charge(request);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		final long tookTimeMills = System.currentTimeMillis() - startTime;
		log.info("소요 시간: {}ms", tookTimeMills);

		// then
		assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedPoint);
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
