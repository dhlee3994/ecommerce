package io.hhplus.ecommerce.payment.domain.outbox;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OutboxStatus {
	PENDING("전송대기"),
	PUBLISHED("전송완료"),
	COMPLETED("처리완료"),
	FAILED("처리실패")
	;
	private final String description;
}
