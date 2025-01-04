package io.hhplus.ecommerce.order.presentation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.ecommerce.global.CommonApiResponse;
import io.hhplus.ecommerce.order.presentation.request.OrderCancelApiRequest;
import io.hhplus.ecommerce.order.presentation.request.OrderCreateApiRequest;
import io.hhplus.ecommerce.order.presentation.response.OrderApiResponse;
import io.hhplus.ecommerce.order.presentation.response.OrderCancelApiResponse;
import io.hhplus.ecommerce.order.presentation.response.OrderCreateApiResponse;
import io.hhplus.ecommerce.order.presentation.response.OrderItemApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@RestController
public class OrderApiController implements IOrderApiController {

	@GetMapping
	@Override
	public CommonApiResponse<List<OrderApiResponse>> getOrders(@RequestParam final Long userId) {
		return CommonApiResponse.ok(
			List.of(
				OrderApiResponse
					.builder()
					.orderId(1L)
					.amount(10000)
					.couponId(1L)
					.couponName("쿠폰1")
					.discountAmount(1000)
					.totalAmount(9000)
					.status("주문완료")
					.orderedAt(LocalDateTime.now())
					.build()
			)
		);
	}

	@GetMapping("/{orderId}")
	@Override
	public CommonApiResponse<List<OrderItemApiResponse>> getOrders(
		@PathVariable final Long orderId,
		@RequestParam final Long userId
	) {
		return CommonApiResponse.ok(
			List.of(
				OrderItemApiResponse
					.builder()
					.orderItemId(1L)
					.productId(2L)
					.name("상품1")
					.price(10000)
					.quantity(1)
					.amount(10000)
					.couponId(1L)
					.couponName("쿠폰1")
					.discountAmount(1000)
					.totalAmount(9000)
					.build()
			)
		);
	}

	@PostMapping
	@Override
	public CommonApiResponse<OrderCreateApiResponse> createOrder(@RequestBody final OrderCreateApiRequest request) {
		return CommonApiResponse.ok(
			OrderCreateApiResponse
				.builder()
				.orderId(1L)
				.amount(10000)
				.couponId(1L)
				.couponName("쿠폰1")
				.discountAmount(1000)
				.totalAmount(9000)
				.status("주문완료")
				.orderedAt(LocalDateTime.now())
				.build()
		);
	}

	@PostMapping("/cancel")
	@Override
	public CommonApiResponse<OrderCancelApiResponse> cancelOrder(@RequestBody final OrderCancelApiRequest request) {
		return CommonApiResponse.ok(
			OrderCancelApiResponse
				.builder()
				.orderId(1L)
				.refundPoint(10000)
				.canceledAt(LocalDateTime.now())
				.build()
		);
	}
}
