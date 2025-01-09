package io.hhplus.ecommerce.order.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.order.application.OrderApplicationService;
import io.hhplus.ecommerce.order.application.response.OrderCreateResponse;
import io.hhplus.ecommerce.order.presentation.request.OrderCreateApiRequest;
import io.hhplus.ecommerce.order.presentation.response.OrderCreateApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@RestController
public class OrderApiController implements IOrderApiController {

	private final OrderApplicationService orderApplicationService;

	@PostMapping
	@Override
	public CommonApiResponse<OrderCreateApiResponse> createOrder(@RequestBody final OrderCreateApiRequest request) {
		final OrderCreateResponse response = orderApplicationService.order(request.toServiceRequest());
		return CommonApiResponse.ok(OrderCreateApiResponse.from(response));
	}
}
