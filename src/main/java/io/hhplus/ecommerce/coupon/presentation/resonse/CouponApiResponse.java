package io.hhplus.ecommerce.coupon.presentation.resonse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "쿠폰 단건 조회, 목록 응답")
@Getter
public class CouponApiResponse {

	@Schema(description = "쿠폰 ID", example = "1")
	private final long couponId;

	@Schema(description = "쿠폰명", example = "쿠폰1")
	private final String name;

	@Schema(description = "쿠폰 대상", example = "상품")
	private final String target;

	@Schema(description = "할인 타입", example = "정률")
	private final String discountType;

	@Schema(description = "할인양", example = "10")
	private final int discountValue;

	@Schema(description = "최대 할인 금액", example = "2000")
	private final int maxDiscount;

	@Builder
	private CouponApiResponse(
		final long couponId, final String name, final String target, final String discountType, final int discountValue,
		final int maxDiscount
	) {
		this.couponId = couponId;
		this.name = name;
		this.target = target;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.maxDiscount = maxDiscount;
	}
}
