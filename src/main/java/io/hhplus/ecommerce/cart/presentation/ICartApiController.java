package io.hhplus.ecommerce.cart.presentation;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.hhplus.ecommerce.cart.presentation.request.CartItemAddApiRequest;
import io.hhplus.ecommerce.cart.presentation.request.CartItemUpdateApiRequest;
import io.hhplus.ecommerce.cart.presentation.response.CartItemAddApiResponse;
import io.hhplus.ecommerce.cart.presentation.response.CartItemApiResponse;
import io.hhplus.ecommerce.cart.presentation.response.CartItemUpdateApiResponse;
import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.global.openapi.ApiFailResponse;
import io.hhplus.ecommerce.global.openapi.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "장바구니 API")
public interface ICartApiController {

	@ApiSuccessResponse(CartItemApiResponse.class)
	@Operation(
		summary = "장바구니 조회",
		description = "장바구니에 담긴 상품 목록을 조회한다.",
		parameters = {
			@Parameter(name = "userId", description = "사용자 아이디", in = QUERY, required = true, example = "1")
		}
	)
	@GetMapping
	CommonApiResponse<List<CartItemApiResponse>> getCartItems(
		@RequestParam final Long userId
	);

	@ApiFailResponse("유효하지 않은 상품입니다.")
	@ApiSuccessResponse(CartItemAddApiResponse.class)
	@Operation(
		summary = "장바구니에 상품 추가",
		description = "장바구니에 상품을 추가한다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = CartItemAddApiRequest.class)
			)
		)
	)
	@PostMapping
	CommonApiResponse<CartItemAddApiResponse> addCartItem(
		@RequestBody CartItemAddApiRequest addRequest
	);

	@ApiFailResponse("상품의 재고가 부족합니다.")
	@ApiSuccessResponse(CartItemUpdateApiResponse.class)
	@Operation(
		summary = "장바구니 상품 수량 변경",
		description = "장바구니에 담긴 상품의 수량을 변경한다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = CartItemUpdateApiRequest.class)
			)
		)
	)
	@PutMapping
	CommonApiResponse<CartItemUpdateApiResponse> updateCartItem(
		@RequestBody CartItemUpdateApiRequest updateRequest
	);
}
