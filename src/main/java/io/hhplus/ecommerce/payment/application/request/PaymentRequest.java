package io.hhplus.ecommerce.payment.application.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PaymentRequest {

	private final Long orderId;
	private final Long userId;
	private final Long couponId;
}
