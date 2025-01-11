package io.hhplus.ecommerce.order.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.global.openapi.ApiSuccessResponse;
import io.hhplus.ecommerce.order.presentation.request.OrderCreateApiRequest;
import io.hhplus.ecommerce.order.presentation.response.OrderCreateApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "주문 API")
public interface IOrderApiController {

	@ApiSuccessResponse(OrderCreateApiResponse.class)
	@Operation(
		summary = "주문 생성",
		description = "상품 상세에서 주문을 생성한다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = OrderCreateApiRequest.class)
			)
		)
	)
	@PostMapping
	CommonApiResponse<OrderCreateApiResponse> createOrder(
		@RequestBody OrderCreateApiRequest request
	);
}
