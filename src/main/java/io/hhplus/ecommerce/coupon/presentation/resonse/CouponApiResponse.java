package io.hhplus.ecommerce.coupon.presentation.resonse;

import io.hhplus.ecommerce.coupon.application.response.CouponResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "쿠폰 단건 조회 응답")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CouponApiResponse {

	@Schema(description = "쿠폰 ID", example = "1")
	private final long couponId;

	@Schema(description = "쿠폰명", example = "쿠폰1")
	private final String name;

	@Schema(description = "쿠폰 최대 발급 수량", example = "30")
	private final int issueLimit;

	@Schema(description = "발급 가능 수량", example = "10")
	private final int quantity;

	@Schema(description = "할인 정책", example = "FIXED")
	private final String discountType;

	@Schema(description = "할인양", example = "1000")
	private final int discountValue;

	public static CouponApiResponse from(final CouponResponse couponResponse) {
		return CouponApiResponse.builder()
			.couponId(couponResponse.getCouponId())
			.name(couponResponse.getName())
			.issueLimit(couponResponse.getIssueLimit())
			.quantity(couponResponse.getQuantity())
			.discountType(couponResponse.getDiscountType())
			.discountValue(couponResponse.getDiscountValue())
			.build();
	}
}
