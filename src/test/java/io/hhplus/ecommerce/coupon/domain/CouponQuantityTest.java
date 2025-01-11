package io.hhplus.ecommerce.coupon.domain;

import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_QUANTITY_IS_NOT_ENOUGH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.hhplus.ecommerce.global.exception.EcommerceException;

class CouponQuantityTest {

	@DisplayName("쿠폰 수량 객체를 생성할 수 있다.")
	@Test
	void createCouponQuantity() throws Exception {
		// given
		final Long couponId = 1L;
		final int quantity = 10;

		// when
		final CouponQuantity result = CouponQuantity.builder()
			.couponId(couponId)
			.quantity(quantity)
			.build();

		// then
		assertThat(result).isNotNull()
			.extracting("couponId", "quantity")
			.containsExactly(couponId, quantity);
	}

	@DisplayName("쿠폰 발급에 성공하면 쿠폰 발급 수량이 1 줄어든다.")
	@Test
	void issueCoupon() throws Exception {
		// given
		final int quantity = 10;
		final CouponQuantity couponQuantity = CouponQuantity.builder()
			.quantity(quantity)
			.build();

		final int expectedQuantity = quantity - 1;

		// when
		couponQuantity.issueCoupon();

		// then
		assertThat(couponQuantity.getQuantity()).isEqualTo(expectedQuantity);
	}

	@DisplayName("발급 가능한 쿠폰이 0이하시 발급요청을 하면 EcommerceException 예외가 발생한다.")
	@Test
	void issueCouponQuantityNotEnough() throws Exception {
		// given
		final int quantity = 0;
		final CouponQuantity couponQuantity = CouponQuantity.builder()
			.quantity(quantity)
			.build();

		// when & then
		assertThatThrownBy(() -> couponQuantity.issueCoupon())
			.isInstanceOf(EcommerceException.class)
			.hasMessage(COUPON_QUANTITY_IS_NOT_ENOUGH.getMessage());
	}
}
