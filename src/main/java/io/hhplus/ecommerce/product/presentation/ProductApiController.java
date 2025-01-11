package io.hhplus.ecommerce.product.presentation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.global.CommonApiResponses;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;
import io.hhplus.ecommerce.product.application.ProductApplicationService;
import io.hhplus.ecommerce.product.application.response.BestProductResponse;
import io.hhplus.ecommerce.product.application.response.ProductResponse;
import io.hhplus.ecommerce.product.presentation.request.ProductSearchApiRequest;
import io.hhplus.ecommerce.product.presentation.response.BestProductApiResponse;
import io.hhplus.ecommerce.product.presentation.response.ProductApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@RestController
public class ProductApiController implements IProductApiController {

	private final ProductApplicationService productApplicationService;

	@GetMapping
	@Override
	public CommonApiResponses<ProductApiResponse> getProducts(
		@ModelAttribute final ProductSearchApiRequest request,
		final Pageable pageable
	) {
		final Page<ProductResponse> products = productApplicationService.getProducts(request.toServiceRequest(), pageable);
		return CommonApiResponses.ok(products, ProductApiResponse::from);
	}

	@GetMapping("/{productId}")
	@Override
	public CommonApiResponse<ProductApiResponse> getProduct(@PathVariable final Long productId) {
		if (productId == null || productId <= 0) {
			throw new InvalidRequestException(ErrorCode.PRODUCT_ID_SHOULD_BE_POSITIVE);
		}
		final ProductResponse product = productApplicationService.getProduct(productId);
		return CommonApiResponse.ok(ProductApiResponse.from(product));
	}

	@GetMapping("/best")
	@Override
	public CommonApiResponse<List<BestProductApiResponse>> getBestProducts() {
		final List<BestProductResponse> bestProducts = productApplicationService.getBestProducts();
		return CommonApiResponse.ok(bestProducts.stream()
			.map(BestProductApiResponse::from)
			.toList());
	}
}
