package io.hhplus.ecommerce.product.infra;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.ecommerce.global.cache.CacheProperty;
import io.hhplus.ecommerce.product.application.response.BestProductResponse;
import io.hhplus.ecommerce.product.domain.ProductCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProductCacheRepositoryImpl implements ProductCacheRepository {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public List<BestProductResponse> getBestProducts() {
		try {
			final Object cachedData = redisTemplate.opsForValue().get(CacheProperty.BestProduct.getCacheKey());
			if (cachedData == null) {
				return null;
			}

			final String jsonValue = objectMapper.writeValueAsString(cachedData);
			return objectMapper.readValue(jsonValue, new TypeReference<>() {});
		} catch (final RedisConnectionFailureException e) {
			log.error("레디스 연결 실패: {}", e.getMessage());
			return null;
		} catch (final Exception e) {
			log.error("레디스 캐시 조회 실패: {}", e.getMessage());
			return null;
		}
	}

	@Override
	public void saveBestProducts(List<BestProductResponse> bestProducts) {
		try {
			redisTemplate.opsForValue()
				.set(CacheProperty.BestProduct.getCacheKey(), bestProducts, CacheProperty.BestProduct.TIMEOUT);
		} catch (final RedisConnectionFailureException e) {
			log.error("레디스 연결 실패: {}", e.getMessage());
		} catch (final Exception e) {
			log.error("레디스 캐시 저장 실패: {}", e.getMessage());
		}
	}

	/**
	 * 코드 리뷰를 위해 남긴다.<br/>
	 * global.cache.CacheProperty 클래스를 사용해라.
	 */
	@Deprecated
	@RequiredArgsConstructor
	enum ProductCacheProperty {

		BEST_PRODUCT("best", (86400L + 3600));

		private final String key;
		private final long timeoutSeconds;

		public String getCacheKey() {
			return "product::" + key;
		}

		public Duration getTimeout() {
			return Duration.ofSeconds(timeoutSeconds);
		}
	}
}
