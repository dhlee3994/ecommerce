package io.hhplus.ecommerce.coupon.presentation.resonse;

import java.time.LocalDateTime;

import io.hhplus.ecommerce.coupon.application.response.IssuedCouponResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "발급된 쿠폰 응답")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IssuedCouponApiResponse {

	@Schema(description = "쿠폰 ID", example = "1")
	private final long couponId;

	@Schema(description = "쿠폰명", example = "쿠폰1")
	private final String name;

	@Schema(description = "할인금액", example = "1000")
	private final int discountAmount;

	@Schema(description = "쿠폰 만료일", example = "2025-01-01 10:00:00")
	private final LocalDateTime expiredAt;

	public static IssuedCouponApiResponse from(final IssuedCouponResponse issuedCouponResponse) {
		return IssuedCouponApiResponse.builder()
			.couponId(issuedCouponResponse.getCouponId())
			.name(issuedCouponResponse.getName())
			.discountAmount(issuedCouponResponse.getDiscountAmount())
			.expiredAt(issuedCouponResponse.getExpiredAt())
			.build();
	}
}
