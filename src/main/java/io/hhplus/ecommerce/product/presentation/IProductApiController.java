package io.hhplus.ecommerce.product.presentation;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.global.CommonApiResponses;
import io.hhplus.ecommerce.global.openapi.ApiFailResponse;
import io.hhplus.ecommerce.global.openapi.ApiSuccessResponse;
import io.hhplus.ecommerce.product.presentation.request.ProductSearchApiRequest;
import io.hhplus.ecommerce.product.presentation.response.BestProductApiResponse;
import io.hhplus.ecommerce.product.presentation.response.ProductApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "상품 API")
public interface IProductApiController {

	@ApiSuccessResponse(value = ProductApiResponse.class, isList = true)
	@Operation(
		summary = "상품 목록 조회",
		description = "상품 목록을 조회한다.",
		parameters = {
			@Parameter(name = "상품 조회 조건", in = QUERY, example = "name=상품1"),
			@Parameter(name = "페이지 조건", in = QUERY, example = "page=1&size=20")
		}
	)
	@GetMapping
	CommonApiResponses<ProductApiResponse> getProducts(
		@ModelAttribute final ProductSearchApiRequest request,
		final Pageable pageable
	);

	@ApiFailResponse("상품을 찾을 수 없습니다.")
	@ApiSuccessResponse(ProductApiResponse.class)
	@Operation(
		summary = "상품 단건 조회",
		description = "상품 아이디로 상품을 조회한다.",
		parameters = {
			@Parameter(name = "상품 아이디", in = PATH, required = true, example = "1")
		}
	)
	@GetMapping("/{productId}")
	CommonApiResponse<ProductApiResponse> getProduct(@PathVariable final Long productId);

	@ApiSuccessResponse(BestProductApiResponse.class)
	@Operation(summary = "베스트 상품 조회", description = "베스트 상품을 조회한다.")
	@GetMapping("/best")
	CommonApiResponse<List<BestProductApiResponse>> getBestProducts();
}
