package io.hhplus.ecommerce.product.presentation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.product.presentation.response.BestProductApiResponse;
import io.hhplus.ecommerce.product.presentation.response.ProductApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@RestController
public class ProductApiController implements IProductApiController {

	@GetMapping
	@Override
	public CommonApiResponse<List<ProductApiResponse>> getProducts() {
		return CommonApiResponse.ok(
			List.of(
				ProductApiResponse
					.builder()
					.productId(1L)
					.name("상품1")
					.price(10000)
					.quantity(100)
					.status("판매중")
					.build(),
				ProductApiResponse
					.builder()
					.productId(2L)
					.name("상품2")
					.price(100000)
					.quantity(10)
					.status("판매중")
					.build()
			)
		);
	}

	@GetMapping("/{productId}")
	@Override
	public CommonApiResponse<ProductApiResponse> getProduct(@PathVariable final Long productId) {
		return CommonApiResponse.ok(
			ProductApiResponse
				.builder()
				.productId(1L)
				.name("상품1")
				.price(10000)
				.quantity(100)
				.status("판매중")
				.build()
		);
	}

	@GetMapping("/best")
	@Override
	public CommonApiResponse<List<BestProductApiResponse>> getBestProducts(
		@RequestParam final LocalDate baseDate
	) {
		return CommonApiResponse.ok(
			List.of(
				BestProductApiResponse
					.builder()
					.productId(1L)
					.name("상품1")
					.totalSales(100)
					.totalAmount(1000000)
					.build()
			)
		);
	}
}
