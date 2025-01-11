package io.hhplus.ecommerce.product.presentation.request;

import io.hhplus.ecommerce.product.application.request.ProductSearchRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "상품 검색 요청")
@Builder
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class ProductSearchApiRequest {

	@Schema(description = "검색 상품명", example = "상품1")
	private final String name;

	public ProductSearchRequest toServiceRequest() {
		return ProductSearchRequest
			.builder()
			.name(name)
			.build();
	}
}
