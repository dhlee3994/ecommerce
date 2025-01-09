package io.hhplus.ecommerce.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	// 공통
	INVALID_REQUEST("잘못된 요청입니다."),

	// 유저
	USER_NOT_FOUND("해당 유저가 존재하지 않습니다."),

	// 포인트
	POINT_NOT_FOUND("해당 유저의 포인트를 조회할 수 없습니다."),
	POINT_SHOULD_BE_POSITIVE("보유 포인트는 1이상이어야 합니다."),
	CHARGE_POINT_SHOULD_BE_POSITIVE("충전 포인트는 1이상이어야 합니다."),
	POINT_IS_NOT_ENOUGH("보유 포인트가 부족합니다."),

	;
	private final String message;
}
