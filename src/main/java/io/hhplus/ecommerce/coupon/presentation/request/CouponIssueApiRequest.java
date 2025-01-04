package io.hhplus.ecommerce.coupon.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "쿠폰 발급 요청")
@Getter
public class CouponIssueApiRequest {

	@Schema(description = "사용자 ID", example = "2")
	private final Long userId;

	@Builder
	private CouponIssueApiRequest(final Long userId) {
		this.userId = userId;
	}
}
