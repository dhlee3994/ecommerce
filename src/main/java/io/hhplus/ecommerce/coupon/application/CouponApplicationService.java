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

	public CouponResponse getCoupon(final Long id) {
		if (id == null || id <= 0) {
			throw new InvalidRequestException(ErrorCode.COUPON_ID_SHOULD_BE_POSITIVE);
		}

		final Coupon coupon = couponRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(COUPON_NOT_FOUND.getMessage()));
		return CouponResponse.from(coupon);
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
