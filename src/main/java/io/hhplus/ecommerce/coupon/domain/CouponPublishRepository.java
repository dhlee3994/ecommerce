package io.hhplus.ecommerce.coupon.domain;

import java.util.Set;

public interface CouponPublishRepository {

	long getRemainingCouponCount(long couponId);

	boolean isAlreadyIssue(long userId, long couponId);

	boolean addCouponQueue(CouponIssueToken token);

	Set<CouponIssueToken> getCouponIssueTokens(int count);

	void decreaseCouponCount(long couponId);

	void addIssuedUser(long userId, long couponId);

	void removeCouponIssueToken(CouponIssueToken token);
}
