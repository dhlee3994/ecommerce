package io.hhplus.ecommerce.coupon.application;

import static io.hhplus.ecommerce.global.exception.ErrorCode.COUPON_NOT_FOUND;
import static io.hhplus.ecommerce.global.exception.ErrorCode.USER_NOT_FOUND;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.ecommerce.coupon.application.request.CouponIssueRequest;
import io.hhplus.ecommerce.coupon.application.response.CouponResponse;
import io.hhplus.ecommerce.coupon.application.response.IssuedCouponResponse;
import io.hhplus.ecommerce.coupon.domain.Coupon;
import io.hhplus.ecommerce.coupon.domain.CouponIssuer;
import io.hhplus.ecommerce.coupon.domain.CouponPublishRepository;
import io.hhplus.ecommerce.coupon.domain.CouponQuantity;
import io.hhplus.ecommerce.coupon.domain.CouponQuantityRepository;
import io.hhplus.ecommerce.coupon.domain.CouponRepository;
import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.coupon.domain.IssuedCouponRepository;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;
import io.hhplus.ecommerce.user.domain.User;
import io.hhplus.ecommerce.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CouponApplicationService {

	private final CouponRepository couponRepository;
	private final CouponQuantityRepository couponQuantityRepository;
	private final IssuedCouponRepository issuedCouponRepository;
	private final UserRepository userRepository;
	private final CouponIssuer couponIssuer;

	private final CouponPublishRepository couponPublishRepository;

	public CouponResponse getCoupon(final Long id) {
		if (id == null || id <= 0) {
			throw new InvalidRequestException(ErrorCode.COUPON_ID_SHOULD_BE_POSITIVE);
		}

		final Coupon coupon = couponRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(COUPON_NOT_FOUND.getMessage()));
		return CouponResponse.from(coupon);
	}

	public void requestIssueCoupon(final CouponIssueRequest request) {
		final long userId = request.getUserId();
		final long couponId = request.getCouponId();

		// 레디스 재고 확인
		final long couponCount = couponPublishRepository.getRemainingCouponCount(couponId);
		if (couponCount <= 0) {
			throw new EcommerceException(ErrorCode.COUPON_QUANTITY_IS_NOT_ENOUGH);
		}

		// 중복 확인
		if (couponPublishRepository.isAlreadyIssue(userId, couponId)) {
			throw new EcommerceException(ErrorCode.COUPON_ALREADY_ISSUED);
		}

		// 발급 대기열에 추가, 추후 스케줄러에서 발급처리(레디스의 쿠폰 재고 감소 및 쿠폰 발급 이력 저장)
		final boolean addSuccess = couponPublishRepository.addCouponQueue(request.toToken());
		if (!addSuccess) {
			throw new EcommerceException(ErrorCode.ALREADY_REQUESTED_COUPON);
		}
	}

	@Transactional
	public IssuedCouponResponse issueCoupon(final CouponIssueRequest request) {

		final User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND.getMessage()));

		final Coupon coupon = couponRepository.findById(request.getCouponId())
			.orElseThrow(() -> new EntityNotFoundException(COUPON_NOT_FOUND.getMessage()));

		final CouponQuantity couponQuantity = couponQuantityRepository.findByCouponIdForUpdate(request.getCouponId())
			.orElseThrow(() -> new EntityNotFoundException(COUPON_NOT_FOUND.getMessage()));

		issuedCouponRepository.findByCouponIdAndUserIdForUpdate(coupon.getId(), user.getId())
			.ifPresent(issuedCoupon -> {
				throw new EcommerceException(ErrorCode.COUPON_ALREADY_ISSUED);
			});

		final IssuedCoupon issuedCoupon = couponIssuer.issue(user, coupon, couponQuantity);
		issuedCouponRepository.save(issuedCoupon);

		return IssuedCouponResponse.of(coupon, issuedCoupon);
	}
}
