package io.hhplus.ecommerce.payment.presentation;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.payment.presentation.request.PaymentApiRequest;
import io.hhplus.ecommerce.payment.presentation.response.PaymentApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@RestController
public class PaymentApiController implements IPaymentApiController {

	@PostMapping
	@Override
	public CommonApiResponse<PaymentApiResponse> payment(
		@RequestBody final PaymentApiRequest request
	) {
		return CommonApiResponse.ok(
			PaymentApiResponse
				.builder()
				.paymentId(1L)
				.amount(10000)
				.payedAt(LocalDateTime.now())
				.build()
		);
	}
}
