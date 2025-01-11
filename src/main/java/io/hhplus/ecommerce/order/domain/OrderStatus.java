package io.hhplus.ecommerce.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

	ORDERED("주문완료"),
	PAID("결제완료");

	private final String description;
}
