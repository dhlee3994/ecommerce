package io.hhplus.ecommerce.coupon.presentation.api;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.hhplus.ecommerce.coupon.application.response.IssuedCouponResponse;
import io.hhplus.ecommerce.coupon.presentation.api.request.CouponIssueApiRequest;
import io.hhplus.ecommerce.coupon.presentation.api.resonse.CouponApiResponse;
import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.global.openapi.ApiFailResponse;
import io.hhplus.ecommerce.global.openapi.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "쿠폰 API")
public interface ICouponApiController {

	@ApiFailResponse("쿠폰을 찾을 수 없습니다.")
	@ApiSuccessResponse(CouponApiResponse.class)
	@Operation(
		summary = "쿠폰 단건 조회",
		description = "쿠폰 아이디로 쿠폰을 조회한다.",
		parameters = {
			@Parameter(name = "couponId", description = "쿠폰 ID", in = PATH, example = "1")
		}
	)
	@GetMapping("/{couponId}")
	CommonApiResponse<CouponApiResponse> getCoupon(@PathVariable Long couponId);

	@ApiFailResponse("유효하지 않은 쿠폰입니다.")
	@Operation(
		summary = "쿠폰 발급",
		description = "사용자가 쿠폰 아이디로 쿠폰을 발급한다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = CouponIssueApiRequest.class)
			)
		)
	)
	@PostMapping("/issue")
	CommonApiResponse<IssuedCouponResponse> issueCoupon(@RequestBody CouponIssueApiRequest issueRequest);
}
