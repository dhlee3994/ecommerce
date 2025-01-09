package io.hhplus.ecommerce.order.presentation.response;

import io.hhplus.ecommerce.order.application.response.OrderCreateResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "주문 생성 응답")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderCreateApiResponse {

	@Schema(description = "주문 ID", example = "1")
	private final Long orderId;

	@Schema(description = "주문 총 금액", example = "10000")
	private final int amount;

	public static OrderCreateApiResponse from(final OrderCreateResponse response) {
		return OrderCreateApiResponse.builder()
			.orderId(response.getOrderId())
			.amount(response.getAmount())
			.build();
	}
}
