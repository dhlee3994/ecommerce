package io.hhplus.ecommerce.point.presentation;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.global.openapi.ApiFailResponse;
import io.hhplus.ecommerce.global.openapi.ApiSuccessResponse;
import io.hhplus.ecommerce.point.presentation.request.PointChargeApiRequest;
import io.hhplus.ecommerce.point.presentation.response.PointApiResponse;
import io.hhplus.ecommerce.point.presentation.response.PointChargeApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트 API")
public interface IPointApiController {

	@ApiSuccessResponse(PointApiResponse.class)
	@Operation(
		summary = "포인트 잔액 조회",
		description = "보유한 포인트 잔액을 조회한다.",
		parameters = {
			@Parameter(name = "userId", description = "사용자 ID", in = QUERY, required = true, example = "1")
		}
	)
	@GetMapping
	CommonApiResponse<PointApiResponse> getPoint(
		@RequestParam final Long userId
	);

	@ApiFailResponse("충전 포인트는 1원 이상이어야 합니다.")
	@ApiSuccessResponse(PointChargeApiResponse.class)
	@Operation(
		summary = "포인트 충전",
		description = "포인트를 충전한다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = PointChargeApiRequest.class)
			)
		)
	)
	@PostMapping("/charge")
	CommonApiResponse<PointChargeApiResponse> chargePoint(
		@RequestBody PointChargeApiRequest request
	);
}
