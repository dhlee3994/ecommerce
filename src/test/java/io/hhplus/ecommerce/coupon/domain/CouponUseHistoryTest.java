package io.hhplus.ecommerce.coupon.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CouponUseHistoryTest {

	@DisplayName("쿠폰 사용 이력 객체를 생성할 수 있다.")
	@Test
	void createCouponUseHistory() throws Exception {
		// given
		final Long issuedCouponId = 1L;
		final Long userId = 1L;
		final Long orderId = 1L;

		// when
		final CouponUseHistory result = CouponUseHistory.builder()
			.issuedCouponId(issuedCouponId)
			.orderId(orderId)
			.build();

		// then
		assertThat(result).isNotNull()
			.extracting("issuedCouponId", "orderId")
			.containsExactly(issuedCouponId, orderId);
	}
}
