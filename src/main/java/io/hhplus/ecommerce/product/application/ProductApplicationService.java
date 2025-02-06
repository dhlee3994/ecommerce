package io.hhplus.ecommerce.product.application;

import static io.hhplus.ecommerce.global.exception.ErrorCode.PRODUCT_NOT_FOUND;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;
import io.hhplus.ecommerce.global.lock.LockInfo;
import io.hhplus.ecommerce.global.lock.LockTemplate;
import io.hhplus.ecommerce.product.application.request.ProductSearchRequest;
import io.hhplus.ecommerce.product.application.response.BestProductResponse;
import io.hhplus.ecommerce.product.application.response.ProductResponse;
import io.hhplus.ecommerce.product.domain.Product;
import io.hhplus.ecommerce.product.domain.ProductCacheRepository;
import io.hhplus.ecommerce.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductApplicationService {

	private final ProductRepository productRepository;
	private final ProductCacheRepository productCacheRepository;
	private final LockTemplate lockTemplate;

	public Page<ProductResponse> getProducts(final ProductSearchRequest request, final Pageable pageable) {
		Page<Product> products = productRepository.getProducts(request.toSearchSpec(), pageable);
		return products.map(ProductResponse::from);
	}

	public ProductResponse getProduct(final Long id) {
		if (id == null || id <= 0) {
			throw new InvalidRequestException(ErrorCode.PRODUCT_ID_SHOULD_BE_POSITIVE);
		}

		final Product product = productRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_FOUND.getMessage()));

		return ProductResponse.from(product);
	}

	public List<BestProductResponse> getBestProducts() {
		List<BestProductResponse> cachedData = productCacheRepository.getBestProducts();
		if (cachedData != null) {
			return cachedData;
		}

		// 캐시 스탬피드 방지를 위해 분산락 적용
		return lockTemplate.execute(new LockInfo("lock:product:best"), () -> {

			// 첫 번째 락 획득 스레드가 캐시에 업데이트 했으므로, 대기 중이던 스레드는 캐시에서 바로 반환
			List<BestProductResponse> refreshedCache = productCacheRepository.getBestProducts();
			if (refreshedCache != null) {
				return refreshedCache;
			}

			final LocalDateTime endDateTime = LocalDateTime.now();
			final LocalDateTime startDateTime = endDateTime.minusDays(3);
			final Pageable pageable = Pageable.ofSize(5);

			List<BestProductResponse> bestProducts =
				productRepository.getBestProducts(startDateTime, endDateTime, pageable)
					.stream()
					.map(BestProductResponse::from)
					.toList();

			productCacheRepository.saveBestProducts(bestProducts);
			return bestProducts;
		}, () -> {
			List<BestProductResponse> fallbackCache = productCacheRepository.getBestProducts();
			return fallbackCache != null ? fallbackCache : List.of();
		});
	}
}
