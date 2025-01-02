package io.hhplus.ecommerce.coupon.presentation.resonse;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "발급된 쿠폰 응답")
@Getter
public class IssuedCouponApiResponse {

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

	@Schema(description = "발급일", example = "2025-01-01 00:00:00")
	private final LocalDateTime issuedAt;

	@Schema(description = "만료일", example = "2025-01-02 00:00:00")
	private final LocalDateTime expiredAt;

	@Builder
	private IssuedCouponApiResponse(
		final long couponId, final String name, final String target, final String discountType, final int discountValue,
		final int maxDiscount,
		final LocalDateTime issuedAt, final LocalDateTime expiredAt
	) {
		this.couponId = couponId;
		this.name = name;
		this.target = target;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.maxDiscount = maxDiscount;
		this.issuedAt = issuedAt;
		this.expiredAt = expiredAt;
	}
}
