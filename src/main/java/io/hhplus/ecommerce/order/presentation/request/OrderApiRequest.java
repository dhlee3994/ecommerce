package io.hhplus.ecommerce.order.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 목록 조회 요청")
@Getter
public class OrderApiRequest {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Builder
	private OrderApiRequest(final Long userId) {
		this.userId = userId;
	}
}
