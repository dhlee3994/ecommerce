package io.hhplus.ecommerce.coupon.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
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
import io.hhplus.ecommerce.coupon.application.request.CouponIssueRequest;
import io.hhplus.ecommerce.coupon.domain.Coupon;
import io.hhplus.ecommerce.coupon.domain.CouponQuantity;
import io.hhplus.ecommerce.coupon.domain.DiscountType;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.user.domain.User;

//@ActiveProfiles("optimistic-lock")
@ActiveProfiles("pessimistic-lock")
class CouponApplicationServiceConcurrencyTest extends ServiceIntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(CouponApplicationServiceConcurrencyTest.class);

	@DisplayName("Lock의 유형별로 성능을 확인하기 위한 테스트이며, 발급 제한이 N개인 쿠폰을 X명의 사용자가 동시에 쿠폰 발급 요청을하고 발급은 N번만 성공한다.(N = X - 1)")
	@RepeatedTest(6)
	void couponThatLimit30Issue40UserForReport() throws Exception {
		// given
		final int tryCount = 5;
		final int issueLimit = tryCount - 1;
		final Coupon coupon = couponJpaRepository.save(Coupon.builder()
			.name("쿠폰A")
			.issueLimit(issueLimit)
			.quantity(issueLimit)
			.discountType(DiscountType.FIXED)
			.discountValue(1000)
			.expiredAt(LocalDateTime.of(2025, 1, 10, 14, 0))
			.build());

		couponQuantityJpaRepository.save(
			CouponQuantity.builder().couponId(coupon.getId()).quantity(issueLimit).build());

		List<CouponIssueRequest> requests = new ArrayList<>();
		for (int i = 0; i < tryCount; i++) {
			final User user = userJpaRepository.save(User.builder().name("유저" + i).build());
			requests.add(CouponIssueRequest.builder()
				.couponId(coupon.getId())
				.userId(user.getId())
				.build());
		}

		final ExecutorService executorService = Executors.newFixedThreadPool(20);
		final CountDownLatch latch = new CountDownLatch(tryCount);

		final long startTime = System.currentTimeMillis();

		// when
		for (int i = 0; i < tryCount; i++) {
			final int index = i;
			executorService.execute(() -> {
				try {
					couponApplicationService.issueCoupon(requests.get(index));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		final long tookTimeMills = System.currentTimeMillis() - startTime;
		log.info("소요 시간: {}ms", tookTimeMills);

		// then
		assertThat(issuedCouponJpaRepository.findAll().size()).isEqualTo(issueLimit);
	}

	@DisplayName("Lock의 유형별로 성능을 확인하기 위한 테스트이며, 동일한 사용자가 하나의 쿠폰을 X번 발급 요청하면 1번만 발급에 성공한다.")
	@RepeatedTest(6)
	void couponIssue5OneUserForReport() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("유저").build());

		final int limit = 30;
		final Coupon coupon = couponJpaRepository.save(Coupon.builder()
			.name("쿠폰A")
			.issueLimit(limit)
			.quantity(limit)
			.discountType(DiscountType.FIXED)
			.discountValue(1000)
			.expiredAt(LocalDateTime.of(2025, 1, 10, 14, 0))
			.build());

		couponQuantityJpaRepository.save(CouponQuantity.builder().couponId(coupon.getId()).quantity(limit).build());

		final CouponIssueRequest request = CouponIssueRequest.builder()
			.couponId(coupon.getId())
			.userId(user.getId())
			.build();

		final int tryCount = 5;
		final ExecutorService executorService = Executors.newFixedThreadPool(20);
		final CountDownLatch latch = new CountDownLatch(tryCount);

		final long startTime = System.currentTimeMillis();

		// when
		for (int i = 0; i < tryCount; i++) {
			executorService.execute(() -> {
				try {
					couponApplicationService.issueCoupon(request);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		final long tookTimeMills = System.currentTimeMillis() - startTime;
		log.info("소요 시간: {}ms", tookTimeMills);

		// then
		assertThat(couponJpaRepository.findById(coupon.getId()).get().getQuantity()).isEqualTo(limit - 1);
		assertThat(couponQuantityJpaRepository.findByCouponId(coupon.getId()).get().getQuantity()).isEqualTo(limit - 1);
		assertThat(issuedCouponJpaRepository.findAll().size()).isEqualTo(1);
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
			.discountType(DiscountType.FIXED)
			.discountValue(1000)
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
			.discountType(DiscountType.FIXED)
			.discountValue(1000)
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
