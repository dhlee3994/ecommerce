package io.hhplus.ecommerce.payment.presentation.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "결제 응답")
@Getter
public class PaymentApiResponse {

	@Schema(description = "결제 ID", example = "1")
	private final Long paymentId;

	@Schema(description = "결제 금액", example = "10000")
	private final int amount;

	@Schema(description = "결제 날짜", example = "2025-01-01 10:00:00")
	private final LocalDateTime payedAt;

	@Builder
	private PaymentApiResponse(final Long paymentId, final int amount, final LocalDateTime payedAt) {
		this.paymentId = paymentId;
		this.amount = amount;
		this.payedAt = payedAt;
	}
}
