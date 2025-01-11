package io.hhplus.ecommerce.payment.application.response;

import io.hhplus.ecommerce.payment.domain.Payment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PaymentResponse {

	private final Long paymentId;
	private final Long orderId;
	private final int paymentPrice;

	public static PaymentResponse from(final Payment payment) {
		return PaymentResponse.builder()
			.paymentId(payment.getId())
			.orderId(payment.getOrderId())
			.paymentPrice(payment.getAmount())
			.build();
	}
}
