package io.hhplus.ecommerce.coupon.presentation.request;

import io.hhplus.ecommerce.coupon.application.request.CouponIssueRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "쿠폰 발급 요청")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CouponIssueApiRequest {

	@Schema(description = "사용자 ID", example = "2")
	private final Long userId;

	@Schema(description = "쿠폰 ID", example = "1")
	private final Long couponId;

	public CouponIssueRequest toServiceRequest() {
		return CouponIssueRequest.builder()
			.userId(userId)
			.couponId(couponId)
			.build();
	}
}
