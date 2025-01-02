package io.hhplus.ecommerce.coupon.presentation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.coupon.presentation.request.CouponIssueApiRequest;
import io.hhplus.ecommerce.coupon.presentation.resonse.CouponApiResponse;
import io.hhplus.ecommerce.coupon.presentation.resonse.CouponIssueApiResponse;
import io.hhplus.ecommerce.coupon.presentation.resonse.IssuedCouponApiResponse;
import io.hhplus.ecommerce.global.CommonApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
@RestController
public class CouponApiController implements ICouponApiController {

	@GetMapping
	@Override
	public CommonApiResponse<List<CouponApiResponse>> getCoupons() {
		return CommonApiResponse.ok(
			List.of(
				CouponApiResponse
					.builder()
					.couponId(1L)
					.name("쿠폰1")
					.target("상품")
					.discountType("정률")
					.discountValue(10)
					.maxDiscount(2000)
					.build(),
				CouponApiResponse
					.builder()
					.couponId(2L)
					.name("쿠폰2")
					.target("주문")
					.discountType("정액")
					.discountValue(3000)
					.build()
			)
		);
	}

	@GetMapping("/{couponId}")
	@Override
	public CommonApiResponse<CouponApiResponse> getCoupon(@PathVariable final Long couponId) {
		return CommonApiResponse.ok(
			CouponApiResponse
				.builder()
				.couponId(1L)
				.name("쿠폰1")
				.target("상품")
				.discountType("정률")
				.discountValue(10)
				.maxDiscount(2000)
				.build()
		);
	}

	@PostMapping("/issue/{couponId}")
	@Override
	public CommonApiResponse<CouponIssueApiResponse> issueCoupon(
		@PathVariable final Long couponId,
		@RequestBody final CouponIssueApiRequest request
	) {
		return CommonApiResponse.ok(
			CouponIssueApiResponse
				.builder()
				.couponId(1L)
				.name("쿠폰1")
				.target("상품")
				.target("상품")
				.discountType("정률")
				.discountValue(10)
				.maxDiscount(2000)
				.issuedAt(LocalDateTime.now())
				.expiredAt(LocalDateTime.now().plusDays(1))
				.build()
		);
	}

	@GetMapping("/issue")
	@Override
	public CommonApiResponse<List<IssuedCouponApiResponse>> getIssuedCoupons(
		@RequestParam final Long userId
	) {
		return CommonApiResponse.ok(
			List.of(
				IssuedCouponApiResponse
					.builder()
					.couponId(1L)
					.name("쿠폰1")
					.target("상품")
					.discountType("정률")
					.discountValue(10)
					.maxDiscount(2000)
					.issuedAt(LocalDateTime.now())
					.expiredAt(LocalDateTime.now().plusDays(1))
					.build()
			)
		);
	}
}
