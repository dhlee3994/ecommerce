package io.hhplus.ecommerce.point.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.point.presentation.request.PointChargeApiRequest;
import io.hhplus.ecommerce.point.presentation.request.PointRefundApiRequest;
import io.hhplus.ecommerce.point.presentation.response.PointApiResponse;
import io.hhplus.ecommerce.point.presentation.response.PointChargeApiResponse;
import io.hhplus.ecommerce.point.presentation.response.PointRefundApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/point")
@RestController
public class PointApiController implements IPointApiController {

	@GetMapping
	@Override
	public CommonApiResponse<PointApiResponse> getPoint(@RequestParam final Long userId) {
		return CommonApiResponse.ok(
			PointApiResponse
				.builder()
				.point(10000)
				.build()
		);
	}

	@PostMapping("/charge")
	@Override
	public CommonApiResponse<PointChargeApiResponse> chargePoint(@RequestBody final PointChargeApiRequest request) {
		return CommonApiResponse.ok(
			PointChargeApiResponse
				.builder()
				.amount(10000)
				.beforePoint(0)
				.afterPoint(10000)
				.build()
		);
	}

	@PostMapping("/refund")
	@Override
	public CommonApiResponse<PointRefundApiResponse> refundPoint(@RequestBody final PointRefundApiRequest request) {
		return CommonApiResponse.ok(
			PointRefundApiResponse
				.builder()
				.amount(10000)
				.beforePoint(10000)
				.afterPoint(0)
				.build()
		);
	}
}
