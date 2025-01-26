package io.hhplus.ecommerce.order.application;

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
import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.order.application.request.OrderCreateRequest;
import io.hhplus.ecommerce.order.application.request.OrderItemCreateRequest;
import io.hhplus.ecommerce.product.domain.Product;
import io.hhplus.ecommerce.product.domain.Stock;
import io.hhplus.ecommerce.user.domain.User;

//@ActiveProfiles("optimistic-lock")
@ActiveProfiles("pessimistic-lock")
class OrderApplicationServiceConcurrencyTest extends ServiceIntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(OrderApplicationServiceConcurrencyTest.class);

	@DisplayName("Lock의 유형별로 성능을 확인하기 위한 테스트이며, 재고가 각각 N, M개인 상품을 1개씩 주문하는 요청을 동시에 X번 요청하고 주문은 N번만 성공한다.(N = X - 1, M = X +1)")
	@RepeatedTest(6)
	void orderManyProductForReport() throws Exception {
		// given
		final int tryCount = 5;
		final int quantityA = tryCount - 1;
		final int quantityB = tryCount + 1;

		final int expectedQuantityA = 0;
		final int expectedQuantityB = quantityB - quantityA;

		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Product productA = productJpaRepository.save(Product.builder().name("상품A").price(100).build());
		final Product productB = productJpaRepository.save(Product.builder().name("상품B").price(100).build());

		final Stock stockA = stockJpaRepository.save(Stock.builder().productId(productA.getId()).quantity(quantityA).build());
		final Stock stockB = stockJpaRepository.save(Stock.builder().productId(productB.getId()).quantity(quantityB).build());

		final int orderQuantity = 1;
		final List<OrderItemCreateRequest> orderItemCreateRequests = List.of(
			OrderItemCreateRequest.builder()
				.productId(productA.getId())
				.quantity(orderQuantity)
				.build(),
			OrderItemCreateRequest.builder()
				.productId(productB.getId())
				.quantity(orderQuantity)
				.build()
		);

		final OrderCreateRequest request = OrderCreateRequest.builder()
			.userId(user.getId())
			.orderItems(orderItemCreateRequests)
			.build();

		final ExecutorService executorService = Executors.newFixedThreadPool(20);
		final CountDownLatch latch = new CountDownLatch(tryCount);

		final long startTime = System.currentTimeMillis();

		// when
		for (int i = 0; i < tryCount; i++) {
			executorService.execute(() -> {
				try {
					orderApplicationService.order(request);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		final long tookTimeMills = System.currentTimeMillis() - startTime;
		log.info("소요 시간: {}ms", tookTimeMills);

		// then
		assertThat(orderJpaRepository.findAll().size()).isEqualTo(quantityA);
		assertThat(stockJpaRepository.findById(stockA.getId()).get().getQuantity()).isEqualTo(expectedQuantityA);
		assertThat(stockJpaRepository.findById(stockB.getId()).get().getQuantity()).isEqualTo(expectedQuantityB);
	}

	@DisplayName("재고가 10개인 상품에 대해 1개씩 20번의 주문 요청이 동시에 들어오면 10개의 요청만 성공한다.")
	@Test
	void orderOneProduct() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Product product = productJpaRepository.save(Product.builder().name("상품A").price(100).build());

		final int quantity = 10;
		stockJpaRepository.save(Stock.builder().productId(product.getId()).quantity(quantity).build());

		final int orderQuantity = 1;
		final List<OrderItemCreateRequest> orderItemCreateRequests = List.of(
			OrderItemCreateRequest.builder()
				.productId(product.getId())
				.quantity(orderQuantity)
				.build()
		);

		final OrderCreateRequest request = OrderCreateRequest.builder()
			.userId(user.getId())
			.orderItems(orderItemCreateRequests)
			.build();

		final int orderTryCount = 20;
		final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(orderTryCount);
		final AtomicInteger exceptionCount = new AtomicInteger(0);

		// when
		for (int i = 1; i <= orderTryCount; i++) {
			tasks.add(CompletableFuture.supplyAsync(() -> {
				orderApplicationService.order(request);
				return true;
			}).exceptionally(e -> {
				if (e.getMessage().contains(ErrorCode.STOCK_QUANTITY_NOT_ENOUGH.getMessage())) {
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
		assertThat(successCount).isEqualTo(10);
		assertThat(failureCount).isEqualTo(exceptionCount.get());
	}

	@DisplayName("재고가 각각 10개, 20개인 상품들에 대해 각 상품당 1개씩 20번의 주문 요청이 동시에 들어오면 10개의 요청만 성공한다.")
	@Test
	void orderManyProduct() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());
		final Product productA = productJpaRepository.save(Product.builder().name("상품A").price(100).build());
		final Product productB = productJpaRepository.save(Product.builder().name("상품B").price(100).build());

		final int quantityA = 10;
		stockJpaRepository.save(Stock.builder().productId(productA.getId()).quantity(quantityA).build());

		final int quantityB = 20;
		stockJpaRepository.save(Stock.builder().productId(productB.getId()).quantity(quantityB).build());

		final int orderQuantity = 1;
		final List<OrderItemCreateRequest> orderItemCreateRequests = List.of(
			OrderItemCreateRequest.builder()
				.productId(productA.getId())
				.quantity(orderQuantity)
				.build(),
			OrderItemCreateRequest.builder()
				.productId(productB.getId())
				.quantity(orderQuantity)
				.build()
		);

		final OrderCreateRequest request = OrderCreateRequest.builder()
			.userId(user.getId())
			.orderItems(orderItemCreateRequests)
			.build();

		final int orderTryCount = 20;
		final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(orderTryCount);
		final AtomicInteger exceptionCount = new AtomicInteger(0);

		// when
		for (int i = 1; i <= orderTryCount; i++) {
			tasks.add(CompletableFuture.supplyAsync(() -> {
				orderApplicationService.order(request);
				return true;
			}).exceptionally(e -> {
				if (e.getMessage().contains(ErrorCode.STOCK_QUANTITY_NOT_ENOUGH.getMessage())) {
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
		assertThat(successCount).isEqualTo(10);
		assertThat(failureCount).isEqualTo(exceptionCount.get());
	}
}
