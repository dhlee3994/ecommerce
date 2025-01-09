package io.hhplus.ecommerce.payment.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.global.openapi.ApiFailResponse;
import io.hhplus.ecommerce.global.openapi.ApiSuccessResponse;
import io.hhplus.ecommerce.payment.presentation.request.PaymentApiRequest;
import io.hhplus.ecommerce.payment.presentation.response.PaymentApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "결제 API")
public interface IPaymentApiController {

	@ApiFailResponse("보유 포인트가 부족합니다.")
	@ApiSuccessResponse(PaymentApiResponse.class)
	@Operation(
		summary = "결제",
		description = "주문에 대한 결제를 한다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = PaymentApiRequest.class)
			)
		)
	)
	@PostMapping
	CommonApiResponse<PaymentApiResponse> payment(@RequestBody PaymentApiRequest request);
}
