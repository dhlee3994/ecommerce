package io.hhplus.ecommerce.coupon.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CouponTest {

	@DisplayName("쿠폰 객체를 생성할 수 있다.")
	@Test
	void createCoupon() throws Exception {
		// given
		final String name = "쿠폰1";
		final int issueLimit = 30;
		final int quantity = 30;
		final int discountAmount = 1000;
		final LocalDateTime expiredAt = LocalDateTime.of(2025, 1, 9, 14, 00);

		// when
		final Coupon result = Coupon.builder()
			.name(name)
			.issueLimit(issueLimit)
			.quantity(quantity)
			.discountAmount(discountAmount)
			.expiredAt(expiredAt)
			.build();

		// then
		assertThat(result).isNotNull()
			.extracting("name", "issueLimit", "quantity", "discountAmount", "expiredAt")
			.containsExactly(name, issueLimit, quantity, discountAmount, expiredAt);
	}

	@DisplayName("쿠폰의 남은 발급 수량을 수정할 수 있다.")
	@Test
	void updateQuantity() throws Exception {
		final Coupon coupon = Coupon.builder()
			.quantity(30)
			.build();

		final int updateQuantity = 29;

		// when
		coupon.updateQuantity(updateQuantity);

		// then
		assertThat(coupon.getQuantity()).isEqualTo(updateQuantity);
	}
}
