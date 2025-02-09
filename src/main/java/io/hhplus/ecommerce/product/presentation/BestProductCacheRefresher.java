package io.hhplus.ecommerce.product.presentation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.hhplus.ecommerce.global.cache.CacheRefresher;
import io.hhplus.ecommerce.product.application.ProductApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class BestProductCacheRefresher implements CacheRefresher {

	private final ProductApplicationService productApplicationService;

	@Scheduled(cron = "0 0 0 * * *")
	public void refreshCache() {
		productApplicationService.getBestProducts();
	}
}
