package io.hhplus.ecommerce.payment.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "결제 요청")
@Getter
public class PaymentApiRequest {

	@Schema(description = "주문 ID", example = "1")
	private final Long orderId;

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Builder
	private PaymentApiRequest(final Long orderId, final Long userId) {
		this.orderId = orderId;
		this.userId = userId;
	}
}
