package io.hhplus.ecommerce.coupon.application.request;

import io.hhplus.ecommerce.coupon.domain.CouponIssueToken;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CouponIssueRequest {
	private final long userId;
	private final long couponId;

	public CouponIssueToken toToken() {
		return new CouponIssueToken(userId, couponId);
	}

	public static CouponIssueRequest from(final CouponIssueToken token) {
		return CouponIssueRequest.builder()
			.userId(token.userId())
			.couponId(token.couponId())
			.build();
	}
}
