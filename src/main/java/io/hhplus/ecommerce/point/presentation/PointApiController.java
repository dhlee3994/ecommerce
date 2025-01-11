package io.hhplus.ecommerce.point.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;
import io.hhplus.ecommerce.point.application.PointApplicationService;
import io.hhplus.ecommerce.point.application.response.PointChargeResponse;
import io.hhplus.ecommerce.point.application.response.PointResponse;
import io.hhplus.ecommerce.point.presentation.request.PointChargeApiRequest;
import io.hhplus.ecommerce.point.presentation.response.PointApiResponse;
import io.hhplus.ecommerce.point.presentation.response.PointChargeApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/point")
@RestController
public class PointApiController implements IPointApiController {


	private final PointApplicationService pointApplicationService;

	@GetMapping
	@Override
	public CommonApiResponse<PointApiResponse> getPoint(@RequestParam final Long userId) {
		if (userId == null || userId <= 0) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}
		PointResponse pointResponse = pointApplicationService.getPoint(userId);
		return CommonApiResponse.ok(PointApiResponse.from(pointResponse));
	}

	@PostMapping("/charge")
	@Override
	public CommonApiResponse<PointChargeApiResponse> chargePoint(@RequestBody final PointChargeApiRequest request) {
		final PointChargeResponse chargeResponse = pointApplicationService.charge(request.toServiceRequest());
		return CommonApiResponse.ok(PointChargeApiResponse.from(chargeResponse));
	}
}
