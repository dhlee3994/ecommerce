package io.hhplus.ecommerce.payment.presentation.request;

import io.hhplus.ecommerce.payment.application.request.PaymentRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "결제 요청")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PaymentApiRequest {

	@Schema(description = "주문 ID", example = "1")
	private final Long orderId;

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Schema(description = "쿠폰 ID", example = "1")
	private final Long couponId;

	public PaymentRequest toServiceRequest() {
		return PaymentRequest.builder()
			.orderId(orderId)
			.userId(userId)
			.couponId(couponId)
			.build();
	}
}
