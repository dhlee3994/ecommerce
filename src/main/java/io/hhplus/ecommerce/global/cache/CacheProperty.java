package io.hhplus.ecommerce.global.cache;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheProperty {

	private static final String CACHE_DELIMITER = "::";

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class BestProduct {
		public static final Duration TIMEOUT = Duration.ofHours(24).plusHours(1);
		public static final String SCHEDULED_CRON_EXPRESSION = "0 0 0 * * *";

		public static String getCacheKey() {
			return "product" + CACHE_DELIMITER + "best";
		}
	}
}
