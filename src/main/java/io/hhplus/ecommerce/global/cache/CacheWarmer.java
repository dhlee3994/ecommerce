package io.hhplus.ecommerce.global.cache;

import jakarta.annotation.PostConstruct;
import java.util.Collection;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 서버가 뜰 때 캐시를 적재하기 위해 사용하는 클래스
 */
@Profile("!test")
@RequiredArgsConstructor
@Component
public class CacheWarmer {

	private final Collection<CacheRefresher> cacheRefreshers;

	@PostConstruct
	public void warmUp() {
		cacheRefreshers.forEach(CacheRefresher::refreshCache);
	}
}
