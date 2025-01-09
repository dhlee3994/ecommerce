package io.hhplus.ecommerce.coupon.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
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

import io.hhplus.ecommerce.coupon.application.request.CouponIssueRequest;
import io.hhplus.ecommerce.coupon.domain.Coupon;
import io.hhplus.ecommerce.coupon.domain.CouponQuantity;
import io.hhplus.ecommerce.coupon.infra.CouponJpaRepository;
import io.hhplus.ecommerce.coupon.infra.CouponQuantityJpaRepository;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.user.domain.User;
import io.hhplus.ecommerce.user.infrastructure.UserJpaRepository;

@ActiveProfiles("testcontainers")
@ImportTestcontainers(TestcontainersConfiguration.class)
@SpringBootTest
class CouponApplicationServiceConcurrencyTest {

	@Autowired
	private CouponApplicationService couponApplicationService;

	@Autowired
	private CouponJpaRepository couponJpaRepository;

	@Autowired
	private CouponQuantityJpaRepository couponQuantityJpaRepository;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@BeforeEach
	void setUp() {
		userJpaRepository.deleteAllInBatch();
		couponJpaRepository.deleteAllInBatch();
		couponQuantityJpaRepository.deleteAllInBatch();
	}

	@DisplayName("쿠폰 발급 제한이 30개인 쿠폰을 40명이 동시에 발급 요청하면 30명만 발급 성공한다.")
	@Test
	void couponThatLimit30Issue40User() throws Exception {
		// given
		final int limit = 30;
		final Coupon coupon = couponJpaRepository.save(Coupon.builder()
			.name("쿠폰A")
			.issueLimit(limit)
			.quantity(limit)
			.expiredAt(LocalDateTime.of(2025, 1, 10, 14, 0))
			.build());

		couponQuantityJpaRepository.save(CouponQuantity.builder()
			.couponId(coupon.getId())
			.quantity(limit)
			.build());

		final int issueTryCount = 40;
		final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(issueTryCount);
		final AtomicInteger exceptionCount = new AtomicInteger(0);

		List<Long> userIds = new ArrayList<>();
		for (int i = 1; i <= issueTryCount; i++) {
			final User user = userJpaRepository.save(User.builder().name("유저" + i).build());
			userIds.add(user.getId());
		}

		// when
		for (int i = 1; i <= issueTryCount; i++) {
			final long userId = userIds.get(i - 1);
			final CouponIssueRequest request = CouponIssueRequest.builder()
				.couponId(coupon.getId())
				.userId(userId)
				.build();

			tasks.add(CompletableFuture.supplyAsync(() -> {
				couponApplicationService.issueCoupon(request);
				return true;
			}).exceptionally(e -> {
				if (e.getMessage().contains(ErrorCode.COUPON_QUANTITY_IS_NOT_ENOUGH.getMessage())) {
					exceptionCount.incrementAndGet();
				}
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

		assertThat(exceptionCount.get()).isEqualTo(10);
		assertThat(successCount).isEqualTo(30);
		assertThat(failureCount).isEqualTo(exceptionCount.get());
	}

	@DisplayName("동일한 사용자가 하나의 쿠폰을 5번 발급 요청하면 1번만 발급에 성공한다.")
	@Test
	void couponIssue5OneUser() throws Exception {
		// given
		final int limit = 30;
		final Coupon coupon = couponJpaRepository.save(Coupon.builder()
			.name("쿠폰A")
			.issueLimit(limit)
			.quantity(limit)
			.expiredAt(LocalDateTime.of(2025, 1, 10, 14, 0))
			.build());

		final CouponQuantity couponQuantity = couponQuantityJpaRepository.save(CouponQuantity.builder()
			.couponId(coupon.getId())
			.quantity(limit)
			.build());

		final int issueTryCount = 5;
		final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(issueTryCount);
		final AtomicInteger exceptionCount = new AtomicInteger(0);

		final User user = userJpaRepository.save(User.builder().name("유저").build());

		// when
		for (int i = 1; i <= issueTryCount; i++) {
			final CouponIssueRequest request = CouponIssueRequest.builder()
				.couponId(coupon.getId())
				.userId(user.getId())
				.build();

			tasks.add(CompletableFuture.supplyAsync(() -> {
				couponApplicationService.issueCoupon(request);
				return true;
			}).exceptionally(e -> {
				if (e.getMessage().contains(ErrorCode.COUPON_ALREADY_ISSUED.getMessage())) {
					exceptionCount.incrementAndGet();
				}
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

		assertThat(exceptionCount.get()).isEqualTo(4);
		assertThat(successCount).isEqualTo(1);
		assertThat(failureCount).isEqualTo(exceptionCount.get());

		assertThat(couponJpaRepository.findById(coupon.getId()).get().getQuantity()).isEqualTo(29);
		assertThat(couponQuantityJpaRepository.findByCouponId(coupon.getId()).get().getQuantity()).isEqualTo(29);
	}
}
