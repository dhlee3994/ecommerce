package io.hhplus.ecommerce.payment.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.payment.application.PaymentApplicationService;
import io.hhplus.ecommerce.payment.application.response.PaymentResponse;
import io.hhplus.ecommerce.payment.presentation.request.PaymentApiRequest;
import io.hhplus.ecommerce.payment.presentation.response.PaymentApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@RestController
public class PaymentApiController implements IPaymentApiController {

	private final PaymentApplicationService paymentApplicationService;

	@PostMapping
	@Override
	public CommonApiResponse<PaymentApiResponse> payment(@RequestBody final PaymentApiRequest request) {
		final PaymentResponse paymentResponse = paymentApplicationService.pay(request.toServiceRequest());
		return CommonApiResponse.ok(PaymentApiResponse.from(paymentResponse));
	}

}
