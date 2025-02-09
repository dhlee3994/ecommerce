package io.hhplus.ecommerce.coupon.presentation.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.hhplus.ecommerce.coupon.application.CouponApplicationService;
import io.hhplus.ecommerce.coupon.application.request.CouponIssueRequest;
import io.hhplus.ecommerce.coupon.domain.CouponIssueToken;
import io.hhplus.ecommerce.coupon.domain.CouponPublishRepository;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponIssueScheduler {

	private static final int BATCH_SIZE = 50;
	private static final int MAX_RETRY_COUNT = 3;

	private final CouponApplicationService couponApplicationService;
	private final CouponPublishRepository couponPublishRepository;

	@Scheduled(fixedDelay = 1000L)
	public void processCouponQueue() {
		final Set<CouponIssueToken> couponIssueTokens = couponPublishRepository.getCouponIssueTokens(BATCH_SIZE);
		if (couponIssueTokens.isEmpty()) {
			return;
		}

		final List<CouponIssueToken> retryQueue = new ArrayList<>();
		for (CouponIssueToken couponIssueToken : couponIssueTokens) {
			try {
				couponApplicationService.issueCoupon(CouponIssueRequest.from(couponIssueToken));

				couponPublishRepository.decreaseCouponCount(couponIssueToken.couponId());
				couponPublishRepository.addIssuedUser(couponIssueToken.userId(), couponIssueToken.couponId());
				couponPublishRepository.removeCouponIssueToken(couponIssueToken);
			} catch (final EcommerceException e) {
				log.warn("쿠폰 발급 실패. 토큰={}", couponIssueToken, e);
				retryQueue.add(couponIssueToken.increaseRetryCount());
			} catch (final Exception e) {
				log.error("쿠폰 발급 스케줄러 에러", e);
				retryQueue.add(couponIssueToken.increaseRetryCount());
			}
		}

		retryQueue.stream()
			.filter(token -> token.retryCount() <= MAX_RETRY_COUNT)
			.forEach(couponPublishRepository::addCouponQueue);
	}
}
