package io.hhplus.ecommerce.payment.application;

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
import io.hhplus.ecommerce.coupon.domain.DiscountType;
import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.order.domain.OrderStatus;
import io.hhplus.ecommerce.payment.application.request.PaymentRequest;
import io.hhplus.ecommerce.point.domain.Point;
import io.hhplus.ecommerce.user.domain.User;

//@ActiveProfiles("optimistic-lock")
@ActiveProfiles("pessimistic-lock")
class PaymentApplicationServiceConcurrencyTest extends ServiceIntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(PaymentApplicationServiceConcurrencyTest.class);

	@DisplayName("Lock의 유형별로 성능을 확인하기 위한 테스트이며, 하나의 결제를 동시에 X번 요청해도 결제는 1번만 성공한다.")
	@RepeatedTest(6)
	void paymentForReport() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Long userId = user.getId();

		final int pointHeld = 10000;
		final int orderAmount = 10000;
		final int expectedBalance = pointHeld - orderAmount;

		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(OrderStatus.ORDERED)
				.build()
		);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(null)
			.build();

		final int tryCount = 5;
		final ExecutorService executorService = Executors.newFixedThreadPool(20);
		final CountDownLatch latch = new CountDownLatch(tryCount);

		final long startTime = System.currentTimeMillis();

		// when
		for (int i = 1; i <= tryCount; i++) {
			executorService.execute(() -> {
				try {
					paymentApplicationService.pay(request);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		final long tookTimeMills = System.currentTimeMillis() - startTime;
		log.info("소요 시간: {}ms", tookTimeMills);

		// then
		assertThat(paymentJpaRepository.findAll().size()).isEqualTo(1);
		assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedBalance);
		assertThat(orderJpaRepository.findById(order.getId()).get().getStatus()).isEqualTo(OrderStatus.PAID);
	}

	@DisplayName("하나의 결제를 동시에 10번 요청해도 1번의 결제만 성공한다.")
	@Test
	void payment() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Long userId = user.getId();

		final int pointHeld = 10000;
		final int orderAmount = 1000;
		final int discountValue = 1000;

		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(OrderStatus.ORDERED)
				.build()
		);

		final long couponId = 1L;
		issuedCouponJpaRepository.save(
			IssuedCoupon.builder()
				.userId(userId)
				.couponId(couponId)
				.discountType(DiscountType.FIXED)
				.discountValue(discountValue)
				.expiredAt(LocalDateTime.now().plusDays(10))
				.build()
		);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(couponId)
			.build();

		final int payTryCount = 10;
		final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(payTryCount);
		final AtomicInteger exceptionCount = new AtomicInteger(0);

		// when
		for (int i = 1; i <= payTryCount; i++) {
			tasks.add(CompletableFuture.supplyAsync(() -> {
				paymentApplicationService.pay(request);
				return true;
			}).exceptionally(e -> {
				if (e.getCause() instanceof EcommerceException) {
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

		assertThat(exceptionCount.get()).isEqualTo(9);
		assertThat(successCount).isEqualTo(1);
		assertThat(failureCount).isEqualTo(exceptionCount.get());
	}

	@DisplayName("쿠폰을 적용하지 않은 채로 동시에 10번 요청해도 1번의 결제만 성공한다.")
	@Test
	void paymentWithNoCoupon() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Long userId = user.getId();

		final int pointHeld = 10000;
		final int orderAmount = 1000;

		pointJpaRepository.save(Point.builder().userId(userId).point(pointHeld).build());

		final Order order = orderJpaRepository.save(
			Order.builder()
				.userId(userId)
				.amount(orderAmount)
				.status(OrderStatus.ORDERED)
				.build()
		);

		final PaymentRequest request = PaymentRequest.builder()
			.orderId(order.getId())
			.userId(userId)
			.couponId(null)
			.build();

		// when
		final int payTryCount = 10;
		final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(payTryCount);
		final AtomicInteger exceptionCount = new AtomicInteger(0);

		// when
		for (int i = 1; i <= payTryCount; i++) {
			tasks.add(CompletableFuture.supplyAsync(() -> {
				paymentApplicationService.pay(request);
				return true;
			}).exceptionally(e -> {
				if (e.getCause() instanceof EcommerceException) {
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

		assertThat(exceptionCount.get()).isEqualTo(9);
		assertThat(successCount).isEqualTo(1);
		assertThat(failureCount).isEqualTo(exceptionCount.get());
	}
}
