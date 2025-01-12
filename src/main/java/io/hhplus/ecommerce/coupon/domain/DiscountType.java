package io.hhplus.ecommerce.coupon.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountType {

	NONE("할인없음"),
	FIXED("정액"),
	RATE("정률");
	private final String description;
}
