package io.hhplus.ecommerce.order.application.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderItemCreateRequest {

	private final Long productId;
	private final int quantity;
}
