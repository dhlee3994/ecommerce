package io.hhplus.ecommerce.cart.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.cart.presentation.request.CartItemAddApiRequest;
import io.hhplus.ecommerce.cart.presentation.request.CartItemUpdateApiRequest;
import io.hhplus.ecommerce.cart.presentation.response.CartItemAddApiResponse;
import io.hhplus.ecommerce.cart.presentation.response.CartItemApiResponse;
import io.hhplus.ecommerce.cart.presentation.response.CartItemUpdateApiResponse;
import io.hhplus.ecommerce.global.CommonApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@RestController
public class CartApiController implements ICartApiController {

	@GetMapping
	@Override
	public CommonApiResponse<List<CartItemApiResponse>> getCartItems(
		@RequestParam final Long userId
	) {
		return CommonApiResponse.ok(
			List.of(
				CartItemApiResponse
					.builder()
					.cartItemId(1L)
					.productId(1L)
					.name("상품1")
					.price(10000)
					.quantity(1)
					.build(),
				CartItemApiResponse
					.builder()
					.cartItemId(2L)
					.productId(2L)
					.name("상품2")
					.price(100000)
					.quantity(1)
					.build()
			)
		);
	}

	@PostMapping
	@Override
	public CommonApiResponse<CartItemAddApiResponse> addCartItem(
		@RequestBody final CartItemAddApiRequest addRequest
	) {
		return CommonApiResponse.ok(
			CartItemAddApiResponse
				.builder()
				.cartItemId(1L)
				.productId(1L)
				.name("상품1")
				.price(10000)
				.quantity(1)
				.build()
		);
	}

	@PutMapping
	@Override
	public CommonApiResponse<CartItemUpdateApiResponse> updateCartItem(
		@RequestBody final CartItemUpdateApiRequest updateRequest
	) {
		return CommonApiResponse.ok(
			CartItemUpdateApiResponse
				.builder()
				.cartItemId(1L)
				.productId(1L)
				.name("상품1")
				.price(10000)
				.quantity(1)
				.build()
		);
	}
}
