package io.hhplus.ecommerce.order.presentation;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.global.openapi.ApiSuccessResponse;
import io.hhplus.ecommerce.order.presentation.request.OrderCancelApiRequest;
import io.hhplus.ecommerce.order.presentation.request.OrderCreateApiRequest;
import io.hhplus.ecommerce.order.presentation.response.OrderApiResponse;
import io.hhplus.ecommerce.order.presentation.response.OrderCancelApiResponse;
import io.hhplus.ecommerce.order.presentation.response.OrderCreateApiResponse;
import io.hhplus.ecommerce.order.presentation.response.OrderItemApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "주문 API")
public interface IOrderApiController {

	@ApiSuccessResponse(OrderApiResponse.class)
	@Operation(
		summary = "주문 목록 조회",
		description = "주문 목록을 조회한다.",
		parameters = {
			@Parameter(name = "userId", description = "사용자 아이디", in = QUERY, required = true, example = "1")
		}
	)
	@GetMapping
	CommonApiResponse<List<OrderApiResponse>> getOrders(
		@RequestParam final Long userId
	);

	@ApiSuccessResponse(OrderItemApiResponse.class)
	@Operation(
		summary = "주문 단건 조회",
		description = "주문 아이디로 주문을 조회한다.",
		parameters = {
			@Parameter(name = "orderId", description = "주문 아이디", in = PATH, required = true, example = "1"),
			@Parameter(name = "userId", description = "사용자 아이디", in = QUERY, required = true, example = "1")
		}
	)
	@GetMapping("/{orderId}")
	CommonApiResponse<List<OrderItemApiResponse>> getOrders(
		@PathVariable final Long orderId,
		@RequestParam final Long userId
	);

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

	@ApiSuccessResponse(OrderCancelApiResponse.class)
	@Operation(
		summary = "주문 취소",
		description = "주문을 취소한다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = OrderCancelApiRequest.class)
			)
		)
	)
	@PostMapping("/cancel")
	CommonApiResponse<OrderCancelApiResponse> cancelOrder(
		@RequestBody OrderCancelApiRequest request
	);
}
