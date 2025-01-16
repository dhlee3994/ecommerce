package io.hhplus.ecommerce.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	// 공통
	INVALID_REQUEST("잘못된 요청입니다."),
	INTERNAL_SERVER_ERROR("시스템 오류가 발생했습니다."),

	// 유저
	USER_NOT_FOUND("해당 유저가 존재하지 않습니다."),

	// 포인트
	POINT_NOT_FOUND("해당 유저의 포인트를 조회할 수 없습니다."),
	POINT_SHOULD_BE_POSITIVE("보유 포인트는 1이상이어야 합니다."),
	CHARGE_POINT_SHOULD_BE_POSITIVE("충전 포인트는 1이상이어야 합니다."),
	POINT_IS_NOT_ENOUGH("보유 포인트가 부족합니다."),

	// 상품
	PRODUCT_ID_SHOULD_BE_POSITIVE("올바르지 않은 상품 아이디입니다."),
	PRODUCT_NOT_FOUND("상품이 존재하지 않습니다."),
	STOCK_QUANTITY_NOT_ENOUGH("상품의 수량이 부족합니다."),

	// 쿠폰
	RATE_DISCOUNT_VALUE_OVER_100("할인 비율은 100%를 넘을 수 없습니다."),
	COUPON_ID_SHOULD_BE_POSITIVE("올바르지 않은 쿠폰 아이디입니다."),
	COUPON_NOT_FOUND("쿠폰을 조회할 수 없습니다."),
	COUPON_QUANTITY_IS_NOT_ENOUGH("쿠폰 발급가능 수량이 부족합니다."),
	COUPON_ALREADY_ISSUED("이미 발급받은 쿠폰입니다."),
	COUPON_IS_EXPIRED("만료된 쿠폰입니다."),
	COUPON_ALREADY_USED("이미 사용한 쿠폰입니다."),

	// 주문
	ORDER_QUANTITY_SHOULD_BE_POSITIVE("상품 주문 수량은 양수여야 합니다."),
	ORDER_NOT_FOUND("주문을 조회할 수 없습니다."),
	ORDER_ALREADY_PAID("해당 주문은 이미 결제가 완료되었습니다."),
	DISCOUNT_AMOUNT_IS_LARGER_THAN_AMOUNT("할인 금액이 총 결제 금액보다 큽니다."),

	;
	private final String message;
}
