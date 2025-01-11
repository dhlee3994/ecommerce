package io.hhplus.ecommerce.order.application;

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

import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.order.application.request.OrderCreateRequest;
import io.hhplus.ecommerce.order.application.request.OrderItemCreateRequest;
import io.hhplus.ecommerce.order.infra.OrderItemJpaRepository;
import io.hhplus.ecommerce.order.infra.OrderJpaRepository;
import io.hhplus.ecommerce.product.domain.Product;
import io.hhplus.ecommerce.product.domain.Stock;
import io.hhplus.ecommerce.product.infra.ProductJpaRepository;
import io.hhplus.ecommerce.product.infra.StockJpaRepository;
import io.hhplus.ecommerce.user.domain.User;
import io.hhplus.ecommerce.user.infrastructure.UserJpaRepository;

@ActiveProfiles("testcontainers")
@ImportTestcontainers(TestcontainersConfiguration.class)
@SpringBootTest
class OrderApplicationServiceConcurrencyTest {

	@Autowired
	private OrderApplicationService orderApplicationService;

	@Autowired
	private OrderJpaRepository orderJpaRepository;
	@Autowired
	private OrderItemJpaRepository orderItemJpaRepository;
	@Autowired
	private ProductJpaRepository productJpaRepository;
	@Autowired
	private StockJpaRepository stockJpaRepository;
	@Autowired
	private UserJpaRepository userJpaRepository;

	@BeforeEach
	void setUp() {
		orderJpaRepository.deleteAllInBatch();
		orderItemJpaRepository.deleteAllInBatch();
		productJpaRepository.deleteAllInBatch();
		stockJpaRepository.deleteAllInBatch();
		userJpaRepository.deleteAllInBatch();
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
