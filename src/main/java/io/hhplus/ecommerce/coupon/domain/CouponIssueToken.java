package io.hhplus.ecommerce.coupon.domain;

public record CouponIssueToken (
	long userId,
	long couponId,
	int retryCount
){
	public CouponIssueToken(long userId, long couponId) {
		this(userId, couponId, 0);
	}

	public CouponIssueToken increaseRetryCount() {
		return new CouponIssueToken(userId, couponId, retryCount + 1);
	}
}
