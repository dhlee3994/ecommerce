package io.hhplus.ecommerce.coupon.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "발급된 쿠폰 요청")
@Getter
public class IssuedCouponApiRequest {

	@Schema(description = "사용자 ID", example = "2")
	private final Long userId;

	@Builder
	private IssuedCouponApiRequest(final Long userId) {
		this.userId = userId;
	}
}
