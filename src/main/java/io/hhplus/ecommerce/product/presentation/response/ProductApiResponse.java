package io.hhplus.ecommerce.product.presentation.response;

import io.hhplus.ecommerce.product.application.response.ProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "상품 단건 조회, 목록 응답")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProductApiResponse {

	@Schema(description = "상품 ID", example = "1")
	private final long id;

	@Schema(description = "상품명", example = "상품1")
	private final String name;

	@Schema(description = "상품 가격", example = "10000")
	private final int price;

	@Schema(description = "상품 수량", example = "100")
	private final int quantity;

	@Schema(description = "상품 상태", example = "판매중")
	private final String status;

	public static ProductApiResponse from(final ProductResponse response) {
		return ProductApiResponse.builder()
			.id(response.getId())
			.name(response.getName())
			.price(response.getPrice())
			.quantity(response.getQuantity())
			.status(response.getStatus())
			.build();
	}
}
