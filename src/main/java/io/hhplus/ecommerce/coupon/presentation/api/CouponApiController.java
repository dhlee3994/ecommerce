package io.hhplus.ecommerce.coupon.presentation.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.coupon.application.CouponApplicationService;
import io.hhplus.ecommerce.coupon.presentation.api.request.CouponIssueApiRequest;
import io.hhplus.ecommerce.coupon.presentation.api.resonse.CouponApiResponse;
import io.hhplus.ecommerce.global.CommonApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
@RestController
public class CouponApiController implements ICouponApiController {

	private final CouponApplicationService couponApplicationService;

	@GetMapping("/{couponId}")
	@Override
	public CommonApiResponse<CouponApiResponse> getCoupon(@PathVariable final Long couponId) {
		return CommonApiResponse.ok(CouponApiResponse.from(couponApplicationService.getCoupon(couponId)));
	}

	@PostMapping("/issue")
	@Override
	public void issueCoupon(@RequestBody final CouponIssueApiRequest request) {
		couponApplicationService.requestIssueCoupon(request.toServiceRequest());
	}
}
