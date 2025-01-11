package io.hhplus.ecommerce.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.order.application.request.OrderCreateRequest;
import io.hhplus.ecommerce.order.application.request.OrderItemCreateRequest;
import io.hhplus.ecommerce.order.application.response.OrderCreateResponse;
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
class OrderApplicationServiceIntegrationTest {

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

	@DisplayName("여러 종류의 상품과 주문 수량을 받아서 주문을 생성할 수 있다.")
	@Test
	void order() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());

		final int priceA = 100;
		final int priceB = 200;
		final int priceC = 300;
		final Product productA = productJpaRepository.save(Product.builder().name("상품A").price(priceA).build());
		final Product productB = productJpaRepository.save(Product.builder().name("상품B").price(priceB).build());
		final Product productC = productJpaRepository.save(Product.builder().name("상품C").price(priceC).build());

		final int quantityA = 10;
		final int quantityB = 20;
		final int quantityC = 30;
		stockJpaRepository.save(Stock.builder().productId(productA.getId()).quantity(quantityA).build());
		stockJpaRepository.save(Stock.builder().productId(productB.getId()).quantity(quantityB).build());
		stockJpaRepository.save(Stock.builder().productId(productC.getId()).quantity(quantityC).build());

		final List<OrderItemCreateRequest> orderItemCreateRequests = List.of(OrderItemCreateRequest.builder()
				.productId(productA.getId())
				.quantity(quantityA)
				.build(),
			OrderItemCreateRequest.builder()
				.productId(productB.getId())
				.quantity(quantityB)
				.build(),
			OrderItemCreateRequest.builder()
				.productId(productC.getId())
				.quantity(quantityC)
				.build());

		final OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
			.userId(user.getId())
			.orderItems(orderItemCreateRequests)
			.build();

		final int expectedAmount = (priceA * quantityA) + (priceB * quantityB) + (priceC * quantityC);

		// when
		final OrderCreateResponse result = orderApplicationService.order(orderCreateRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getOrderId()).isNotNull();
		assertThat(result.getAmount()).isEqualTo(expectedAmount);
	}

	@DisplayName("존재하지 않는 사용자로 주문을 생성하려고 하면 EntityNotFoundException 예외가 발생한다.")
	@Test
	void orderWithInvalidUser() throws Exception {
		// given
		final OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
			.userId(System.currentTimeMillis())
			.build();

		// when & then
		assertThatThrownBy(() -> orderApplicationService.order(orderCreateRequest))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
	}

	@DisplayName("존재하지 않는 상품으로 주문을 생성하려고 하면 EntityNotFoundException 예외가 발생한다.")
	@Test
	void orderWithInvalidProduct() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());

		final OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
			.userId(user.getId())
			.orderItems(List.of(
				OrderItemCreateRequest.builder()
					.productId(System.currentTimeMillis())
					.quantity(10)
					.build()
			))
			.build();

		// when & then
		assertThatThrownBy(() -> orderApplicationService.order(orderCreateRequest))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}

	@DisplayName("주문 생성시 상품 재고가 부족하면 EcommerceException 예외가 발생한다.")
	@Test
	void orderWithQuantity() throws Exception {
		// given
		final User user = userJpaRepository.save(User.builder().name("사용자").build());

		final Product product = productJpaRepository.save(Product.builder().name("상품A").price(100).build());
		final Stock stock = stockJpaRepository.save(
			Stock.builder().productId(product.getId()).quantity(1).build()
		);

		final int orderQuantity = stock.getQuantity() + 1;

		final OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
			.userId(user.getId())
			.orderItems(List.of(
				OrderItemCreateRequest.builder()
					.productId(product.getId())
					.quantity(orderQuantity)
					.build()
			))
			.build();

		// when & then
		assertThatThrownBy(() -> orderApplicationService.order(orderCreateRequest))
			.isInstanceOf(EcommerceException.class)
			.hasMessageContaining(ErrorCode.STOCK_QUANTITY_NOT_ENOUGH.getMessage());
	}
}
