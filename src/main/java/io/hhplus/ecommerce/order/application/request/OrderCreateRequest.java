package io.hhplus.ecommerce.order.application.request;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderCreateRequest {

	private final Long userId;
	private final List<OrderItemCreateRequest> orderItems;

	public List<Long> extractProductIds() {
		return orderItems.stream()
			.map(OrderItemCreateRequest::getProductId)
			.toList();
	}
}
