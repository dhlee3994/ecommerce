package io.hhplus.ecommerce.product.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.ecommerce.common.IntegrationTest;
import io.hhplus.ecommerce.global.cache.CacheProperty;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.order.domain.OrderItem;
import io.hhplus.ecommerce.order.infra.OrderItemJpaRepository;
import io.hhplus.ecommerce.order.infra.OrderJpaRepository;
import io.hhplus.ecommerce.product.application.response.BestProductResponse;
import io.hhplus.ecommerce.product.domain.Product;
import io.hhplus.ecommerce.product.domain.ProductRepository;
import io.hhplus.ecommerce.product.infra.ProductJpaRepository;
import io.hhplus.ecommerce.product.presentation.BestProductCacheRefresher;

public class ProductApplicationServiceCacheTest extends IntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(ProductApplicationServiceCacheTest.class);
	@Autowired
	private ProductApplicationService productApplicationService;

	@Autowired
	private BestProductCacheRefresher bestProductCacheRefresher;

	@MockitoSpyBean
	private ProductRepository productRepository;

	@Autowired
	private ProductJpaRepository productJpaRepository;
	@Autowired
	private OrderJpaRepository orderJpaRepository;
	@Autowired
	private OrderItemJpaRepository orderItemJpaRepository;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		redisTemplate.delete(redisTemplate.keys("*"));

		for (int i = 1; i <= 10; i++) {
			final Product product = productJpaRepository.save(
				Product.builder().name(String.valueOf(i)).price(1000).quantity(i * 10).build()
			);

			final Order order = orderJpaRepository.save(Order.createOrder(1L));
			final OrderItem orderItem = orderItemJpaRepository.save(
				OrderItem.builder()
					.orderId(order.getId())
					.productId(product.getId())
					.productName(product.getName())
					.price(1000)
					.quantity(i)
					.build()
			);
			order.addOrderItem(orderItem);
			orderJpaRepository.save(order);
		}
		orderJpaRepository.flush();
	}

	@DisplayName("인기상품 캐시가 존재하지 않으면 DB에서 데이터를 조회하고, 캐시에 저장한다.")
	@Test
	void getBestProducts() throws Exception {
		// given
		final String key = CacheProperty.BestProduct.getCacheKey();
		assertThat(redisTemplate.opsForValue().get(key)).isNull();

		// when
		productApplicationService.getBestProducts();

		// then
		final Object afterCached = redisTemplate.opsForValue().get(key);
		assertThat(afterCached).isNotNull();

		final List<BestProductResponse> result = objectMapper.readValue(
			objectMapper.writeValueAsString(afterCached),
			new TypeReference<>() {
			}
		);
		assertThat(result).hasSize(5);
	}

	@DisplayName("판매량 상위 상품의 Cache가 Hit하면, 쿼리가 실행되지 않는다.")
	@Test
	void getBestProducts2() throws Exception {
		// given
		final int requestCount = 10;

		// when
		for (int i = 0; i < requestCount; i++) {
			long startTime = System.currentTimeMillis();
			productApplicationService.getBestProducts();
			log.info("{}회 요청 응답시간: {}ms", (i + 1), (System.currentTimeMillis() - startTime));
		}

		// then
		verify(productRepository, times(1))
			.getBestProducts(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
	}

	@DisplayName("인기상품 캐시 데이터 갱신 스케줄러가 실행되면 캐시 데이터가 갱신된다.")
	@Test
	void getBestProductsTTL2() throws Exception {
		// given
		final String key = CacheProperty.BestProduct.getCacheKey();
		assertThat(redisTemplate.opsForValue().get(key)).isNull();

		// when
		bestProductCacheRefresher.refreshCache();

		// then
		assertThat(redisTemplate.opsForValue().get(key)).isNotNull();
	}

	@DisplayName("상품이 없으면 빈 리스트를 반환한다")
	@Test
	void getBestProductsWhenEmpty() throws Exception {
		// given
		orderItemJpaRepository.deleteAll();
		orderJpaRepository.deleteAll();
		productJpaRepository.deleteAll();

		// when
		List<BestProductResponse> result = productApplicationService.getBestProducts();

		// then
		assertThat(result).isEmpty();

		final Object cached = redisTemplate.opsForValue().get(CacheProperty.BestProduct.getCacheKey());
		List<BestProductResponse> cachedResult = objectMapper.readValue(
			objectMapper.writeValueAsString(cached),
			new TypeReference<>() {
			}
		);
		assertThat(cachedResult).isEmpty();

		productApplicationService.getBestProducts();
		verify(productRepository, times(1))
			.getBestProducts(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
	}

	@DisplayName("인기상품 캐시가 없을 때 동시에 여러 요청이 들어와도 DB 조회는 단 한번만 실행되고, 나머지 요청은 캐시에서 데이터를 조회한다. 즉, 캐시 스탬피드 현상이 발생하지 않는다.")
	@Test
	void cacheStampede() throws Exception {
		// given
		final String key = CacheProperty.BestProduct.getCacheKey();
		assertThat(redisTemplate.opsForValue().get(key)).isNull();

		final int requestCount = 100;
		final ExecutorService executorService = Executors.newFixedThreadPool(32);
		final CountDownLatch latch = new CountDownLatch(requestCount);

		// when
		for (int i = 0; i < requestCount; i++) {
			executorService.execute(() -> {
				try {
					productApplicationService.getBestProducts();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// then
		assertThat(redisTemplate.opsForValue().get(key)).isNotNull();

		verify(productRepository, times(1))
			.getBestProducts(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
	}
}
