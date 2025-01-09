package io.hhplus.ecommerce.payment.presentation.response;

import io.hhplus.ecommerce.payment.application.response.PaymentResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "결제 응답")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PaymentApiResponse {

	@Schema(description = "결제 ID", example = "1")
	private final Long paymentId;

	@Schema(description = "주문 ID", example = "1")
	private final Long orderId;

	@Schema(description = "결제 금액", example = "10000")
	private final int paymentPrice;

	public static PaymentApiResponse from(final PaymentResponse paymentResponse) {
		return PaymentApiResponse.builder()
			.paymentId(paymentResponse.getPaymentId())
			.orderId(paymentResponse.getOrderId())
			.paymentPrice(paymentResponse.getPaymentPrice())
			.build();
	}
}
