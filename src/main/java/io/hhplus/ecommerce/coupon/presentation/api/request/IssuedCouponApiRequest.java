package io.hhplus.ecommerce.coupon.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "발급된 쿠폰 요청")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IssuedCouponApiRequest {

	@Schema(description = "사용자 ID", example = "2")
	private final Long userId;
}
