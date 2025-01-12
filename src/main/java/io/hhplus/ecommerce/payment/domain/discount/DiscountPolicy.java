package io.hhplus.ecommerce.payment.domain.discount;

public interface DiscountPolicy {

	int calculateDiscountAmount(int price, int discountValue);
}
